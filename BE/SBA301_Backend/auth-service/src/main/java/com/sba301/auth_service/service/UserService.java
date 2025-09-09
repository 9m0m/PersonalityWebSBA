package com.sba301.auth_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sba301.auth_service.dto.request.CompleteProfileRequest;
import com.sba301.auth_service.dto.request.UserCreationRequest;
import com.sba301.auth_service.dto.request.UserUpdateRequest;
import com.sba301.auth_service.dto.request.VerifyEmailRequest;
import com.sba301.auth_service.dto.response.UserResponse;
import com.sba301.auth_service.entity.OTP;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    void updateUserStatus(String userId);

    UserResponse completeUserProfile(CompleteProfileRequest request);

    UserResponse getUser(String userId);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(String userId, UserUpdateRequest request);

    Page<UserResponse> getUsers(Pageable pageable);

    void resendOtp(String email, OTP.OtpPurpose purpose);

    void verifyOtpEmail(VerifyEmailRequest request);

    void resetPasswordForForgot(String email, String newPassword);

    void verifyForgotOtp(String email, String otpCode);
}
