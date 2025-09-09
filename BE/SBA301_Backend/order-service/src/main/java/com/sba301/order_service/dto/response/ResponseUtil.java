package com.sba301.order_service.dto.response;

import com.sba301.order_service.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> build(
            HttpStatus status, String message, T data) {

        return ResponseEntity.status(status)
                .body(ApiResponse.<T>builder()
                        .code(status.value())
                        .message(message)
                        .data(data)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return build(HttpStatus.OK, "success", data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String msg) {
        return build(HttpStatus.NOT_FOUND, msg, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> conflict(String msg) {
        return build(HttpStatus.CONFLICT, msg, null);
    }
}
