package com.sba301.auth_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {
    String id;

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

    Integer districtCode;
    Integer provinceCode;
}
