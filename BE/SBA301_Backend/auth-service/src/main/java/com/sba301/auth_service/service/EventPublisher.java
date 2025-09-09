package com.sba301.auth_service.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import com.sba301.event.CreatedUserEvent;
import com.sba301.event.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final StreamBridge streamBridge;

    public void sendNotification(NotificationEvent event) {
        streamBridge.send("sendNotification-out-0", event);
    }

    public void sendUserCreated(CreatedUserEvent event) {
        streamBridge.send("sendUserCreated-out-0", event);
    }
}
