package com.sba301.persona_service.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.sba301.event.UserProfileCreationFailedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final StreamBridge streamBridge;

    public void sendProfileCreationFailed(String userId, String reason) {
        UserProfileCreationFailedEvent event = UserProfileCreationFailedEvent.builder()
                .userId(userId)
                .reason(reason)
                .build();

        boolean sent = streamBridge.send("profileCreationFailed-out-0", event);

        if (sent) {
            log.info("Sent UserProfileCreationFailedEvent for user {}", userId);
        } else {
            log.error("Failed to send UserProfileCreationFailedEvent for user {}", userId);
        }
    }
}
