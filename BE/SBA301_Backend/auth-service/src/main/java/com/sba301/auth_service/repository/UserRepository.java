package com.sba301.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sba301.auth_service.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);
}
