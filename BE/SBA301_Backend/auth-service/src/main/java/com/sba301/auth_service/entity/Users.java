package com.sba301.auth_service.entity;

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
public class Users {
    public enum Role {
        STUDENT,
        PARENT,
        EVENT_MANAGER,
        ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true)
    String email;

    String password;

    @Column(name = "email_verified", nullable = false)
    boolean emailVerified;

    @Column(name = "active", nullable = false)
    @Builder.Default
    boolean active = true;

    @Enumerated(EnumType.STRING)
    Role role;
}
