package com.sba301.auth_service.dto.response;

import com.sba301.auth_service.entity.Users;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String email;
    boolean noPassword;
    Users.Role role;
    boolean active;
    boolean emailVerified;
}
