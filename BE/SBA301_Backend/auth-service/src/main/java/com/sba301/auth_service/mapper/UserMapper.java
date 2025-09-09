package com.sba301.auth_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sba301.auth_service.dto.request.UserCreationRequest;
import com.sba301.auth_service.dto.request.UserUpdateRequest;
import com.sba301.auth_service.dto.response.UserResponse;
import com.sba301.auth_service.entity.Users;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toUser(UserCreationRequest request);

    UserResponse toUserResponse(Users user);

    @Mapping(target = "role", ignore = true)
    void updateUser(@MappingTarget Users user, UserUpdateRequest request);

    List<UserResponse> toUserResponseList(List<Users> all);
}
