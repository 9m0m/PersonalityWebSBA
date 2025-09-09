package com.sba301.event_service.mapper;

import com.sba301.event_service.dto.ShowTimeCreateRequest;
import com.sba301.event_service.dto.ShowTimeResponse;
import com.sba301.event_service.dto.ShowTimeUpdateRequest;
import com.sba301.event_service.entity.ShowTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowTimeMapper {
    @Mapping(target = "id", ignore = true)
    ShowTime toShowTime(ShowTimeCreateRequest showTimeCreateRequest);

    @Mapping(target = "tickets", source = "tickets")
    @Mapping(target = "meetingId", ignore = true)
    @Mapping(target = "meetingPassword", ignore = true)
    ShowTime toShowTime(ShowTimeUpdateRequest showTimeUpdateRequest, @MappingTarget ShowTime existingShowTime);

    @Mapping(target = "meetingId", ignore = true)
    @Mapping(target = "meetingPassword", ignore = true)
    ShowTimeResponse toShowTimeResponse(ShowTime showTime);

    ShowTimeResponse toShowTimeResponseWithMeeting(ShowTime showTime);
}
