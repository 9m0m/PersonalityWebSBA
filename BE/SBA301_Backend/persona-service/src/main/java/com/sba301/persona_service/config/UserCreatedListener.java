package com.sba301.persona_service.config;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.sba301.event.CreatedUserEvent;
import com.sba301.persona_service.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final ProfileService profileService;

    @Bean
    public Consumer<CreatedUserEvent> userCreated() {
        return event -> {
            log.info("Received UserCreatedEvent for userId: {}", event.getId());
            profileService.saveProfile(event);
        };
    }
}
