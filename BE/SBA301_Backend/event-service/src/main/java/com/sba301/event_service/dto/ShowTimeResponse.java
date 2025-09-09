package com.sba301.event_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ShowTimeResponse(
        int id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String meetingId,
        String meetingPassword,
        List<TicketResponse> tickets
) {
}
