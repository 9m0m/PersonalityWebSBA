package com.sba301.persona_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sba301.persona_service.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findById(String id);
}
