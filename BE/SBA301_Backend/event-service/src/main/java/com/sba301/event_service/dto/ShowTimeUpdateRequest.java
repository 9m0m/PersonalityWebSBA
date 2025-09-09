package com.sba301.event_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShowTimeUpdateRequest(
        int id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String meetingId,
        String meetingPassword,
        List<TicketUpdateRequest> tickets
) {
}
