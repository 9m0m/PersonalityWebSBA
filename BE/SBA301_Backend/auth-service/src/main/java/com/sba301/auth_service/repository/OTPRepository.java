package com.sba301.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sba301.auth_service.entity.OTP;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmailAndOtpAndUsedFalseAndPurpose(String email, String otp, OTP.OtpPurpose purpose);

    List<OTP> findByEmailAndUsedFalse(String uid);

    List<OTP> findByEmailAndPurposeAndUsedFalse(String email, OTP.OtpPurpose purpose);
}
