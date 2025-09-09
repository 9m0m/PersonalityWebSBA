package com.sba301.persona_service.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Profile {
    @Id
    @Column(name = "id")
    private String id;

    private String fullName;
    private LocalDate birthDate;
    private String avatarUrl;
    private String phone;
    private String address;
    private int districtCode;
    private int provinceCode;
}
