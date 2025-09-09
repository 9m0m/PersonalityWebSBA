package com.sba301.premium_service.dto;

import lombok.Builder;

@Builder
public record PremiumCreateRequest(
        String name,
        String description,
        int price,
        int duration
) {
}
