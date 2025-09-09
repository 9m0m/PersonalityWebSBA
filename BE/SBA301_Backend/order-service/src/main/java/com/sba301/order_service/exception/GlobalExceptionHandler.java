package com.sba301.order_service.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba301.order_service.dto.response.ApiResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(exception.getCode());
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(apiResponse);
    }

    //validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }


        return ResponseEntity.badRequest().body(
                Map.of(
                        "code", 1000,
                        "message", "Field Input Invalid",
                        "data", errors
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "code", 1000,
                                "message", ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException exception) {
        String message = exception.getMessage();
        try {
            String content = exception.contentUTF8();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(content);
            if (node.has("message")) {
                message = node.get("message").asText();
            }
        } catch (Exception ignored) {}
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(400);
        apiResponse.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

}
