package com.sba301.event_service.dto;

import com.sba301.event_service.entity.Event;

import java.time.LocalDateTime;

public record EventOverviewResponse(
        int id,
        String slug,
        String name,
        String bannerUrl,
        LocalDateTime startTime,
        int price,
        Event.EventStatus status
) {
}
