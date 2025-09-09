package com.sba301.auth_service.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sba301.auth_service.entity.Users;
import com.sba301.auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    // phung add
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
        initializeEventManagerUser();
    }

    private void initializeAdminUser() {
        String adminEmail = "admin@sba.vn";

        if (!userRepository.existsByEmail(adminEmail)) {
            Users adminUser = Users.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("12345678"))
                    .role(Users.Role.ADMIN)
                    .emailVerified(true)
                    .active(true)
                    .build();

            userRepository.save(adminUser);
            log.info("Admin user created successfully with email: {}", adminEmail);
        } else {
            log.info("Admin user already exists with email: {}", adminEmail);
        }
    }

    private void initializeEventManagerUser() {
        String eventManagerEmail = "event@sba.vn";

        if (!userRepository.existsByEmail(eventManagerEmail)) {
            Users eventManagerUser = Users.builder()
                    .email(eventManagerEmail)
                    .password(passwordEncoder.encode("12345678"))
                    .role(Users.Role.EVENT_MANAGER)
                    .emailVerified(true)
                    .active(true)
                    .build();

            userRepository.save(eventManagerUser);
            log.info("Event Manager user created successfully with email: {}", eventManagerEmail);
        } else {
            log.info("Event Manager user already exists with email: {}", eventManagerEmail);
        }
    }
}
