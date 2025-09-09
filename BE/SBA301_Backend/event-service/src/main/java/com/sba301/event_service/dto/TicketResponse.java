package com.sba301.event_service.dto;

import com.sba301.event_service.entity.Ticket;

import java.time.LocalDateTime;

public record TicketResponse(
        int id,
        String name,
        String description,
        double price,
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Ticket.TicketStatus status
) {
}
