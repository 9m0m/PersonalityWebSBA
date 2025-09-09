package com.sba301.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "Email has been used", HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED(1009, "Password existed", HttpStatus.BAD_REQUEST),
    UNVERIFIED_EMAIL(1010, "Unverified email", HttpStatus.UNAUTHORIZED),
    OTP_EXPIRED(1011, "OTP expired", HttpStatus.UNAUTHORIZED),
    PROFILE_CREATION_FAILED(1012, "Profile creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_ACTIVE(1013, "User is not active", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_ACTIVE(1014, "User is already active", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DEACTIVATED(1015, "User is already deactivated", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
