package com.sba301.event_service.dto;

import lombok.Builder;

@Builder
public record EventRegistrantRequest(
        String email
) {
}
