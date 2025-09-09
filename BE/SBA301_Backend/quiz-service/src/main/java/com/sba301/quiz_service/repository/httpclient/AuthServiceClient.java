package com.sba301.quiz_service.repository.httpclient;

import com.sba301.quiz_service.dto.external.UserResponse;
import com.sba301.quiz_service.dto.external.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {
    @GetMapping(value = "/users/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getUserByEmail(@RequestParam("email") String email, @RequestHeader("Authorization") String authorizationHeader);

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId, @RequestHeader("Authorization") String authorizationHeader);
}
