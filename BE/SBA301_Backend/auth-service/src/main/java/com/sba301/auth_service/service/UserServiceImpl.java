package com.sba301.auth_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sba301.auth_service.dto.request.CompleteProfileRequest;
import com.sba301.auth_service.dto.request.UserCreationRequest;
import com.sba301.auth_service.dto.request.UserUpdateRequest;
import com.sba301.auth_service.dto.request.VerifyEmailRequest;
import com.sba301.auth_service.dto.response.UserResponse;
import com.sba301.auth_service.entity.OTP;
import com.sba301.auth_service.entity.Users;
import com.sba301.auth_service.exception.AppException;
import com.sba301.auth_service.exception.ErrorCode;
import com.sba301.auth_service.mapper.UserMapper;
import com.sba301.auth_service.repository.OTPRepository;
import com.sba301.auth_service.repository.UserRepository;
import com.sba301.event.CreatedUserEvent;
import com.sba301.event.NotificationEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    EventPublisher eventPublisher;
    OTPRepository otpRepository;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new AppException(ErrorCode.EMAIL_EXISTED);

        Users user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getIsParent() ? Users.Role.PARENT : Users.Role.STUDENT);
        user = userRepository.save(user);

        String otp = generateOtp(user, OTP.OtpPurpose.VERIFY_EMAIL);
        eventPublisher.sendNotification(NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject(OTP.OtpPurpose.VERIFY_EMAIL.getEmailSubject())
                .templateCode(OTP.OtpPurpose.VERIFY_EMAIL.getTemplateCode())
                .param(Map.of("OTP", otp, "PURPOSE", OTP.OtpPurpose.VERIFY_EMAIL.getDescription()))
                .build());

        eventPublisher.sendUserCreated(new CreatedUserEvent(
                user.getId(),
                request.getFullName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getAddress(),
                request.getDistrictCode(),
                request.getProvinceCode()));

        return userMapper.toUserResponse(user);
    }

    public void updateUserStatus(String userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public UserResponse completeUserProfile(CompleteProfileRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!StringUtils.hasText(user.getPassword()) && StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setRole(request.getIsParent() ? Users.Role.PARENT : Users.Role.STUDENT);

        userRepository.save(user);

        // Gửi thông tin qua profile-service
        eventPublisher.sendUserCreated(new CreatedUserEvent(
                user.getId(),
                request.getFullName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getAddress(),
                request.getDistrictCode(),
                request.getProvinceCode()));

        return userMapper.toUserResponse(user);
    }

    public UserResponse getUser(String userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));

        return userResponse;
    }

    public UserResponse getUserByEmail(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userMapper.updateUser(user, request);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getUsers(Pageable pageable) {
        log.info("In method getUsers with paging");

        Page<Users> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userMapper::toUserResponse);
    }

    private String generateOtp(Users user, OTP.OtpPurpose purpose) {
        List<OTP> oldOtps = otpRepository.findByEmailAndUsedFalse(user.getEmail());
        for (OTP oldOtp : oldOtps) {
            oldOtp.setUsed(true);
        }
        otpRepository.saveAll(oldOtps);
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000)); // 6 chữ số
        OTP token = OTP.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusSeconds(180)) // 3 phút
                .purpose(purpose)
                .build();

        otpRepository.save(token);
        return otp;
    }

    public void verifyOtpEmail(VerifyEmailRequest request) {
        validateOtp(request.getEmail(), request.getOtpCode(), OTP.OtpPurpose.VERIFY_EMAIL);

        Users user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setEmailVerified(true);
        userRepository.save(user);
        eventPublisher.sendNotification(NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject("Welcome!")
                .templateCode("welcome_email")
                .param(Map.of("user", user.getEmail()))
                .build());
    }

    @Transactional
    public void resendOtp(String email, OTP.OtpPurpose purpose) {
        if (userRepository.existsByEmail(email)) {
            List<OTP> oldOtps = otpRepository.findByEmailAndPurposeAndUsedFalse(email, purpose);
            for (OTP oldOtp : oldOtps) {
                oldOtp.setUsed(true);
            }
            otpRepository.saveAll(oldOtps);

            // Sinh OTP mới
            String newOtpCode = String.valueOf((int) ((Math.random() * 900000) + 100000));
            OTP newOtp = OTP.builder()
                    .email(email)
                    .otp(newOtpCode)
                    .expiryTime(LocalDateTime.now().plusSeconds(60))
                    .purpose(purpose)
                    .build();
            otpRepository.save(newOtp);

            eventPublisher.sendNotification(NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(email)
                    .subject(purpose.getEmailSubject())
                    .templateCode(purpose.getTemplateCode())
                    .param(Map.of("OTP", newOtp.getOtp(), "PURPOSE", purpose.getDescription()))
                    .build());
            log.info("Resent OTP for email {} with purpose {}", email, purpose);
        } else {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    @Transactional
    public void resetPasswordForForgot(String email, String newPassword) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Mật khẩu đã được cập nhật cho người dùng {}", email);
    }

    @Transactional
    public void verifyForgotOtp(String email, String otpCode) {
        validateOtp(email, otpCode, OTP.OtpPurpose.RESET_PASSWORD);
        log.info("OTP xác thực cho quên mật khẩu thành công cho email {}", email);
    }

    private void validateOtp(String email, String otpCode, OTP.OtpPurpose purpose) {
        OTP token = otpRepository
                .findByEmailAndOtpAndUsedFalseAndPurpose(email, otpCode, purpose)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_EXPIRED));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        token.setUsed(true);
        otpRepository.save(token);
    }
}
