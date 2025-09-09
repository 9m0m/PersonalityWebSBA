package com.sba301.auth_service.listener;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.sba301.auth_service.repository.UserRepository;
import com.sba301.event.UserProfileCreationFailedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileEventListener {

    private final UserRepository userRepository;

    @Bean
    public Consumer<UserProfileCreationFailedEvent> profileCreationFailed() {
        return event -> {
            log.warn("Received rollback event for user {}", event.getUserId());
            if (userRepository.existsById(event.getUserId())) {
                userRepository.deleteById(event.getUserId());
                log.info("Rolled back user {}", event.getUserId());
            } else {
                log.warn("User {} not found, skip rollback", event.getUserId());
            }
        };
    }
}
