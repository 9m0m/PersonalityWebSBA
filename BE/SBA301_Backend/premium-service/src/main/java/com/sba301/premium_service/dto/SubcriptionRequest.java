package com.sba301.premium_service.dto;

import lombok.Builder;

@Builder
public record SubcriptionRequest(
        int premiumId,
        String uid
) {
}
