package com.sba301.notification.service;

import com.sba301.notification.dto.request.SendEmailRequest;
import com.sba301.notification.exception.AppException;
import com.sba301.notification.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    public void send(SendEmailRequest email) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            Context context = new Context();
            if (email.getTemplateData() != null) {
                context.setVariables(email.getTemplateData());
            }

            // Process template
            String htmlContent = templateEngine.process(email.getTemplateName(), context);
            helper.setText(htmlContent, true);



            javaMailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

}

