package com.sba301.event_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ShowTimeCreateRequest(
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<TicketCreateRequest> tickets
) {
}
