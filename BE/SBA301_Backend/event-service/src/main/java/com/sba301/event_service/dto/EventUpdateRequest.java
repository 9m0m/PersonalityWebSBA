package com.sba301.event_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record EventUpdateRequest(
        int id,
        String name,
        String slug,
        String description,
        @JsonAlias("banner_url")
        String bannerUrl,
        @JsonAlias("personality_types")
        String personalityTypes,
        List<ShowTimeUpdateRequest> showtimes,

        @JsonAlias("bank_account_name")
        String bankAccountName,
        @JsonAlias("bank_account_number")
        String bankAccountNumber,
        @JsonAlias("bank_name")
        String bankName,
        @JsonAlias("bank_branch")
        String bankBranch,

        @JsonAlias("vat_business_type")
        String vatBusinessType,
        @JsonAlias("vat_holder_name")
        String vatHolderName,
        @JsonAlias("vat_holder_address")
        String vatHolderAddress,
        @JsonAlias("tax_code")
        String taxCode
) {
}
