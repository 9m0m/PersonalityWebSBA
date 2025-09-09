package com.sba301.order_service.dto.request;

import lombok.Builder;

@Builder
public record SubcriptionRequest(
        int premiumId,
        String uid
) {
}
