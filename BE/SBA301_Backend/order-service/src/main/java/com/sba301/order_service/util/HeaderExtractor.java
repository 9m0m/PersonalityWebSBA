package com.sba301.order_service.util;

import com.sba301.order_service.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.Map;

@UtilityClass
public class HeaderExtractor {

    public String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new AppException(401, "User ID is missing in the request header", HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }

    public String getUserEmail(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
        if (email == null || email.isEmpty()) {
            throw new AppException(401, "User email is missing in the request header", HttpStatus.UNAUTHORIZED);
        }
        return email;
    }

    public Map<String, String> getHeaders(HttpServletRequest request) {
        return Map.of(
                "X-User-Id", getUserId(request),
                "X-User-Email", getUserEmail(request)
        );
    }
}
