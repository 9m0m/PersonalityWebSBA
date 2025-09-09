package com.sba301.event_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record EventApproveRequest(
        int id,
        @JsonAlias("is_approved")
        boolean isApproved,
        String notes
) {
}
