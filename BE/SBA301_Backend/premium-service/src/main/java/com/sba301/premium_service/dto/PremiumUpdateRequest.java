package com.sba301.premium_service.dto;

import com.sba301.premium_service.entity.Premium;

public record PremiumUpdateRequest(
        int id,
        String name,
        String description,
        int price,
        int duration,
        Premium.PremiumStatus status
) {
}
