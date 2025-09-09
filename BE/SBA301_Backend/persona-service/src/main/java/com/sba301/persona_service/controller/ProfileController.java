package com.sba301.persona_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sba301.event.CreatedUserEvent;
import com.sba301.persona_service.config.AuthContext;
import com.sba301.persona_service.dto.ProfileResponse;
import com.sba301.persona_service.entity.Profile;
import com.sba301.persona_service.service.ProfileService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile API")
@CrossOrigin(
        origins = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH})
public class ProfileController {
    private final ProfileService profileService;
    private final AuthContext authContext;

    @GetMapping()
    public ResponseEntity<Profile> getProfile() {
        return ResponseEntity.ok(profileService.getProfileById(authContext.getUserId()));
    }

    @PatchMapping()
    @PreAuthorize("@authContext.getUserId() == #profile.id")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody CreatedUserEvent request) {
        return ResponseEntity.ok(profileService.saveProfile(request));
    }
}
