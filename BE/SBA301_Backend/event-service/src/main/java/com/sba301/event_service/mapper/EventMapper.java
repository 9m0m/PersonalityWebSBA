package com.sba301.event_service.mapper;

import com.sba301.event_service.dto.*;
import com.sba301.event_service.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    Event toEvent(EventCreateRequest eventCreateRequest);

    Event toEvent(EventUpdateRequest eventUpdateRequest, @MappingTarget Event existingEvent);

    @Mapping(target = "startTime", expression = "java(getNextUpcomingStartTime(event))")
    @Mapping(target = "price", expression = "java(getFirstTicketPrice(event))")
    EventOverviewResponse toEventOverviewResponse(Event event);
    EventPublicDetailResponse toEventPublicDetailResponse(Event event);
    EventPrivateDetailResponse toEventPrivateDetailResponse(Event event);
    EventSubmissionResponse toEventSubmissionResponse(Event event);

    default LocalDateTime getNextUpcomingStartTime(Event event) {
        System.out.println(event.getShowtimes().getFirst().getStartTime());
        return event.getShowtimes().stream()
                .filter(showTime -> showTime.getStartTime().isAfter(LocalDateTime.now()))
                .map(showTime -> showTime.getStartTime())
                .findFirst()
                .orElse(
                        event.getShowtimes().stream()
                                .map(showTime -> showTime.getStartTime())
                                .max(LocalDateTime::compareTo)
                                .orElse(LocalDateTime.now())
                );
    }

    default Integer getFirstTicketPrice(Event event) {
        return event.getShowtimes().stream()
                .findFirst()
                .flatMap(showtime -> showtime.getTickets().stream().findFirst())
                .map(ticket -> ticket.getPrice())
                .orElse(0);
    }
}
