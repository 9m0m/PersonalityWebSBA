package com.sba301.event_service.service;

import com.sba301.event_service.dto.*;
import org.springframework.data.domain.Page;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public interface IEventService {

    Page<EventOverviewResponse> getEvents(
            String name,
            LocalDate from,
            LocalDate to,
            String organizerId,
            String moderatorId,
            String status,
            String personalityTypes,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    EventPrivateDetailResponse getEventById(int id);

    EventPublicDetailResponse getEventBySlug(String slug);

    void createDraftEvent(EventCreateRequest eventCreateRequest);

    URI createAndSubmitEvent(EventCreateRequest eventCreateRequest);

    EventSubmissionResponse submitDraftEvent(int id, EventUpdateRequest eventUpdateRequest);

    EventPrivateDetailResponse approveEvent(int id, EventApproveRequest eventApproveRequest);

    EventPrivateDetailResponse updateDraftEvent(int id, EventUpdateRequest eventUpdateRequest);

    EventPrivateDetailResponse rejectEvent(int id, EventApproveRequest eventApproveRequest);

    EventPrivateDetailResponse cancelEvent(int id, EventApproveRequest eventApproveRequest);

    EventPrivateDetailResponse updateEvent(int id, EventUpdateRequest eventUpdateRequest);

    void updateEventStatus();

    TicketResponse getTicketById(int id);

    List<TicketResponse> getTicketsByShowTimeId(int showTimeId);

    TicketResponse updateEventTicketQuantity(int id, int minusQuantity);
}
