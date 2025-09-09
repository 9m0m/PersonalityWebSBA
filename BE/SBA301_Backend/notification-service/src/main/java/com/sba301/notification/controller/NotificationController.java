package com.sba301.notification.controller;


import com.sba301.event.NotificationEvent;
import com.sba301.notification.dto.request.SendEmailRequest;
import com.sba301.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;

    @Bean
    public Consumer<NotificationEvent> notificationDelivery() {
        return event -> {
            log.info("Received notification event: {}", event);
            emailService.send(SendEmailRequest.builder()
                    .to(event.getRecipient())
                    .subject(event.getSubject())
                    .templateName(event.getTemplateCode())
                    .templateData(event.getParam())
                    .build());
        };
    }
}
