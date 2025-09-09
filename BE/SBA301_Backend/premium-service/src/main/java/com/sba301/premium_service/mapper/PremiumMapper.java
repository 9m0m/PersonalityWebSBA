package com.sba301.premium_service.mapper;

import com.sba301.premium_service.dto.PremiumCreateRequest;
import com.sba301.premium_service.dto.PremiumResponse;
import com.sba301.premium_service.dto.PremiumUpdateRequest;
import com.sba301.premium_service.entity.Premium;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PremiumMapper {

    PremiumResponse toPremiumResponse(Premium premium);

    Premium toPremium(PremiumCreateRequest premiumCreateRequest);
    Premium toPremium(PremiumUpdateRequest premiumUpdateRequest, @MappingTarget Premium premium);
}
