package com.sba301.order_service.dto.response;

import java.time.LocalDateTime;

public record TicketResponse (
    int id,
    String name,
    String description,
    double price,
    int quantity,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String status
){}
