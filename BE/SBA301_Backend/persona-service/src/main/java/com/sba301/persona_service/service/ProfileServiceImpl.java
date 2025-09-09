package com.sba301.persona_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sba301.event.CreatedUserEvent;
import com.sba301.persona_service.config.AuthContext;
import com.sba301.persona_service.dto.ProfileResponse;
import com.sba301.persona_service.entity.Profile;
import com.sba301.persona_service.mapper.ProfileMapper;
import com.sba301.persona_service.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AuthContext authContext;
    private final ProfileMapper profileMapper;
    private final EventPublisher eventPublisher;

    @Override
    public ProfileResponse saveProfile(CreatedUserEvent request) {
        boolean isNewUser = !profileRepository.existsById(request.getId());
        try {
            profileRepository.save(profileMapper.toProfile(request));
        } catch (Exception e) {
            log.error(
                    "Profile {} failed for user {}, reason: {}",
                    isNewUser ? "creation" : "update",
                    request.getId(),
                    e.getMessage());
            if (isNewUser) {
                eventPublisher.sendProfileCreationFailed(request.getId(), "DB error: " + e.getMessage());
            }
        }
        return profileMapper.toProfileResponse(request);
    }

    @Override
    public Profile getProfileById(String uid) {

        return profileRepository
                .findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for UID: " + uid));
    }
}
