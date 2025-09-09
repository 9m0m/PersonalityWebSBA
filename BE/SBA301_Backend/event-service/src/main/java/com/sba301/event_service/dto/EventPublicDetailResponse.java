package com.sba301.event_service.dto;

import com.sba301.event_service.entity.Event;
import lombok.Builder;

import java.util.List;

@Builder
public record EventPublicDetailResponse(
        int id,
        String slug,
        String name,
        String description,
        String bannerUrl,
        String organizerId,
        Event.EventStatus status,
        String personalityTypes,
        List<ShowTimeResponse> showtimes
) {
}
