package com.sba301.event_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TicketCreateRequest(
        String name,
        String description,
        int price,
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
