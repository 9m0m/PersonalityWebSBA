package com.sba301.auth_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    String email;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @NotBlank(message = "FULL_NAME_REQUIRED")
    String fullName;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(regexp = "^\\d{9,11}$", message = "INVALID_PHONE")
    String phone;

    @NotNull(message = "BIRTHDATE_REQUIRED")
    @Past(message = "BIRTHDATE_MUST_BE_PAST")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate;

    @NotBlank(message = "ADDRESS_REQUIRED")
    String address;

    int districtCode;
    int provinceCode;
    Boolean isParent;
}
