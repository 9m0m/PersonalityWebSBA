package com.sba301.premium_service.mapper;

import com.sba301.premium_service.dto.SubcriptionRequest;
import com.sba301.premium_service.dto.SubcriptionResponse;
import com.sba301.premium_service.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubcriptionMapper {
    Subscription toSubscription(SubcriptionRequest subcriptionRequest);

    @Mapping(target = "premiumId", source = "premium.id")
    @Mapping(target = "premiumName", source = "premium.name")
    SubcriptionResponse toSubcriptionResponse(Subscription subscription);
}
