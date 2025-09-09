package com.sba301.event_service.dto;

import java.time.LocalDateTime;

public record EventStatsResponse(
        int id,
        String slug,
        String name,
        LocalDateTime startTime,
        int totalTickets,
        int totalTicketsSold,
        int revenue
) {
}
