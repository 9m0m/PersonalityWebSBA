package com.sba301.auth_service.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteProfileRequest {
    String fullName;
    String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate;

    String phone;
    String address;
    Integer districtCode;
    Integer provinceCode;
    Boolean isParent;
}
