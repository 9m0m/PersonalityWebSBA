package com.sba301.persona_service.mapper;

import org.mapstruct.Mapper;

import com.sba301.event.CreatedUserEvent;
import com.sba301.persona_service.dto.ProfileResponse;
import com.sba301.persona_service.entity.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfile(CreatedUserEvent profileDto);

    ProfileResponse toProfileResponse(CreatedUserEvent profile);
}
