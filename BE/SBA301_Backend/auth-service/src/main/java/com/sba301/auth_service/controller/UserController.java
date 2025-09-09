package com.sba301.auth_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.sba301.auth_service.dto.ApiResponse;
import com.sba301.auth_service.dto.request.*;
import com.sba301.auth_service.dto.response.UserResponse;
import com.sba301.auth_service.entity.OTP;
import com.sba301.auth_service.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(@PageableDefault(size = 10, sort = "email") Pageable pageable) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getUsers(pageable))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponse user = userService.getUser(userId);
        return ApiResponse.<UserResponse>builder().result(user).build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable String id) {
        userService.updateUserStatus(id);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/by-email")
    public ApiResponse<UserResponse> getUserByEmail(@RequestParam("email") String email) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByEmail(email))
                .build();
    }

    @PostMapping("/complete-profile")
    public ApiResponse<UserResponse> completeProfile(@RequestBody CompleteProfileRequest request) {
        UserResponse response = userService.completeUserProfile(request);
        return ApiResponse.<UserResponse>builder().result(response).build();
    }

    @PostMapping("/resend")
    public ApiResponse<String> resendOtp(@RequestParam String email, @RequestParam OTP.OtpPurpose purpose) {
        userService.resendOtp(email, purpose);
        return ApiResponse.<String>builder()
                .result("OTP đã được gửi lại thành công.")
                .build();
    }

    @PostMapping("/verify-otp")
    ApiResponse<String> verifyEmail(@RequestBody VerifyEmailRequest request) {
        userService.verifyOtpEmail(request);
        return ApiResponse.<String>builder().result("Xác thực thành công.").build();
    }

    @PostMapping("/forgot-password/reset")
    ApiResponse<String> resetForgotPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPasswordForForgot(request.getEmail(), request.getNewPassword());

        return ApiResponse.<String>builder().result("Mật khẩu đã được đặt lại.").build();
    }

    @PostMapping("/forgot-password/verify")
    public ApiResponse<String> verifyForgotOtp(@RequestParam String email, @RequestParam String otpCode) {
        userService.verifyForgotOtp(email, otpCode);
        return ApiResponse.<String>builder()
                .result("OTP hợp lệ. Vui lòng tiếp tục đặt lại mật khẩu.")
                .build();
    }
}
