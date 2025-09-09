package com.sba301.auth_service.mapper;

import org.mapstruct.Mapper;

import com.sba301.auth_service.dto.request.ProfileCreationRequest;
import com.sba301.auth_service.dto.request.UserCreationRequest;
import com.sba301.auth_service.entity.Users;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(Users user, UserCreationRequest request);
}
