package com.sba301.premium_service.dto;

import com.sba301.premium_service.entity.Subscription;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SubcriptionResponse(
        int id,
        String uid,
        int premiumId,
        String premiumName,
        LocalDate startDate,
        LocalDate endDate,
        Subscription.SubscriptionStatus status
) {
}
