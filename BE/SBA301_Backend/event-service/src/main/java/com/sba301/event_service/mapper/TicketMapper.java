package com.sba301.event_service.mapper;

import com.sba301.event_service.dto.TicketCreateRequest;
import com.sba301.event_service.dto.TicketResponse;
import com.sba301.event_service.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    Ticket toTicket(TicketCreateRequest ticketCreateRequest);

    Ticket toTicket(TicketCreateRequest ticketCreateRequest, @MappingTarget Ticket existingTicket);

    TicketResponse toTicketResponse(Ticket ticket);
}
