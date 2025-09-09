package com.sba301.event_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.sba301.event_service.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public record EventPrivateDetailResponse(
        int id,
        String slug,
        String name,
        String description,
        String bannerUrl,
        boolean approved,
        Event.EventStatus status,
        String organizerId,
        String moderatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String notes,
        String personalityTypes,
        List<ShowTimeResponse> showtimes,

        String bankAccountName,
        String bankAccountNumber,
        String bankName,
        String bankBranch,

        String vatBusinessType,
        String vatHolderName,
        String vatHolderAddress,
        String taxCode
) {
}
