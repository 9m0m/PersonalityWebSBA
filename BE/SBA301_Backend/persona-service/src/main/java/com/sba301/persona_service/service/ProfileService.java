package com.sba301.persona_service.service;

import com.sba301.event.CreatedUserEvent;
import com.sba301.persona_service.dto.ProfileResponse;
import com.sba301.persona_service.entity.Profile;

public interface ProfileService {
    ProfileResponse saveProfile(CreatedUserEvent request);

    Profile getProfileById(String id);
}
