package com.sba301.persona_service.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record ProfileResponse(
        String id,
        String fullName,
        LocalDate birthDate, // Use String for date format consistency
        String avatarUrl,
        String phone,
        String address,
        String district,
        String province) {}
