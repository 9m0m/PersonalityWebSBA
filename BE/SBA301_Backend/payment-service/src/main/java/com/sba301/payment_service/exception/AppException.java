package com.sba301.payment_service.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class AppException extends RuntimeException{
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    public AppException(int code, String message, HttpStatusCode statusCode) {
        super(message);
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}