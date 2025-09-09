package com.sba301.auth_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class OTP {
    @Getter
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public enum OtpPurpose {
        VERIFY_EMAIL("Xác thực email", "otp_verified", "Xác thực email"),
        RESET_PASSWORD("Khôi phục mật khẩu", "otp_verified", "Khôi phục mật khẩu"),
        UPDATE_PROFILE("Xác thực đổi thông tin", "otp_update_profile", "Xác thực đổi thông tin"),
        WELLCOME("Chào mừng bạn đến với SBA301", "wellcome_email", "Chào mừng bạn đến với SBA301");

        String description;
        String templateCode;
        String emailSubject;

        OtpPurpose(String description, String templateCode, String emailSubject) {
            this.description = description;
            this.templateCode = templateCode;
            this.emailSubject = emailSubject;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String otp;

    @Enumerated(EnumType.STRING)
    OtpPurpose purpose;

    String email;
    LocalDateTime expiryTime;
    boolean used = false;
}
