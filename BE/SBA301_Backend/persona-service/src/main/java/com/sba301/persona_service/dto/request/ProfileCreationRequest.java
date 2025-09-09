package com.sba301.persona_service.dto.request;

import java.time.LocalDate;

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
    String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate;

    String phone;
    String address;
    Integer districtCode;
    Integer provinceCode;
}
