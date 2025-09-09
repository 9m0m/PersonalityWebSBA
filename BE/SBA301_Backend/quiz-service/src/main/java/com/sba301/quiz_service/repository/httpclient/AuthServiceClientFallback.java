package com.sba301.quiz_service.repository.httpclient;

import com.sba301.quiz_service.dto.external.*;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    @Override
    public ApiResponse<UserResponse> getUserByEmail(String email, String authorizationHeader) {
        return ApiResponse.<UserResponse>builder()
                .code(503)
                .message("Auth service is currently unavailable")
                .result(null)
                .build();
    }
    @Override
    public ApiResponse<UserResponse> getUserById(String userId, String authorizationHeader) {
        return ApiResponse.<UserResponse>builder()
                .code(503)
                .message("Auth service is currently unavailable")
                .result(null)
                .build();
    }
}
