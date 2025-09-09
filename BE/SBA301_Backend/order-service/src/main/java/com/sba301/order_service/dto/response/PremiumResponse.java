package com.sba301.order_service.dto.response;

public record PremiumResponse(
        int id,
        String name,
        String description,
        int price,
        int duration,
        String status
) {
}
