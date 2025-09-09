package com.sba301.event_service.controller;

import com.sba301.event_service.dto.*;
import com.sba301.event_service.service.IEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
@Tag(name = "Event", description = "Event Management API")
@RequiredArgsConstructor
public class EventController {
    private final IEventService eventService;

    @Operation(summary = "Get paginated list of events", description = "Retrieve a paginated list of events with optional filters")
    @GetMapping
    public ResponseEntity<Page<EventOverviewResponse>> eventPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String organizerId,
            @RequestParam(required = false) String moderatorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String personalityTypes,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(eventService.getEvents(name, from, to, organizerId, moderatorId, status, personalityTypes, page, size, sortBy, sortDirection));
    }

    @Operation(summary = "Get event by ID", description = "Get event details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventPrivateDetailResponse> getEventById(@PathVariable int id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @Operation(summary = "Get event by slug", description = "Get event details by slug")
    @GetMapping("/slug/{slug:[a-z0-9\\\\-]+}")
    public ResponseEntity<EventPublicDetailResponse> getEventBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.getEventBySlug(slug));
    }

    @Operation(summary = "Create draft event", description = "Organizer creates a new draft event")
    @PostMapping
    public ResponseEntity<Void> createDraftEvent(@RequestBody EventCreateRequest eventCreateRequest) {
        eventService.createDraftEvent(eventCreateRequest);
        return ResponseEntity
                .created(null)
                .build();
    }

    @Operation(summary = "Save changes draft event", description = "Organizer submits an event for approval")
    @PutMapping
    public ResponseEntity<EventSubmissionResponse> createAndSubmitEvent(@RequestBody EventCreateRequest eventCreateRequest) {
        return ResponseEntity
                .created(eventService.createAndSubmitEvent(eventCreateRequest))
                .build();
    }

    @Operation(summary = "Update draft event", description = "Organizer updates a draft event")
    @PutMapping("/{id}")
    public ResponseEntity<EventPrivateDetailResponse> updateDraftEvent(@PathVariable int id, @RequestBody EventUpdateRequest eventUpdateRequest) {
        return ResponseEntity.ok(eventService.updateDraftEvent(id, eventUpdateRequest)) ;
    }

    @Operation(summary = "Submit draft event", description = "Organizer submits a draft event for approval")
    @PutMapping("/{id}/submit")
    public ResponseEntity<EventSubmissionResponse> createAndSubmitEvent(@PathVariable int id, @RequestBody EventUpdateRequest eventUpdateRequest) {
        return ResponseEntity.ok(eventService.submitDraftEvent(id, eventUpdateRequest));
    }

    @Operation(summary = "Approve event", description = "Moderator approves an event")
    @PutMapping("/{id}/approve")
    public ResponseEntity<EventPrivateDetailResponse> approveEvent(@PathVariable int id, @RequestBody EventApproveRequest eventApproveRequest) {
        return ResponseEntity.ok(eventService.approveEvent(id, eventApproveRequest));
    }

    @Operation(summary = "Reject event", description = "Moderator rejects an event")
    @PutMapping("/{id}/reject")
    public ResponseEntity<EventPrivateDetailResponse> rejectEvent(@PathVariable int id, @RequestBody EventApproveRequest eventApproveRequest) {
        return ResponseEntity.ok(eventService.rejectEvent(id, eventApproveRequest));
    }

    @Operation(summary = "Cancel event", description = "Moderator/Organizer cancels an event")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<EventPrivateDetailResponse> cancelEvent(@PathVariable int id, @RequestBody EventApproveRequest eventApproveRequest) {
        return ResponseEntity.ok(eventService.cancelEvent(id, eventApproveRequest));
    }

    @Operation(summary = "Update event", description = "Moderator/Organizer updates an event")
    @PatchMapping("/{id}")
    public ResponseEntity<EventPrivateDetailResponse> updateEvent(@PathVariable int id, @RequestBody EventUpdateRequest eventUpdateRequest) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventUpdateRequest));
    }

    @GetMapping("/tickets/{id}")
    @Operation(summary = "Get event tickets", description = "Get ticket")
    public ResponseEntity<TicketResponse> getEventTickets(@PathVariable int id) {
        return ResponseEntity.ok(eventService.getTicketById(id));
    };

    @GetMapping("/showtimes/{id}/tickets")
    @Operation(summary = "Get event showtime tickets", description = "Get tickets for a specific showtime")
    public ResponseEntity<List<TicketResponse>> getEventShowtimeTickets(@PathVariable int id) {
        return ResponseEntity.ok(eventService.getTicketsByShowTimeId(id));
    }


    @PostMapping("/tickets/{id}/quantity/{minusQuantity}")
    ResponseEntity<TicketResponse> updateEventTicketQuantity(@PathVariable("id") Integer id, @PathVariable("minusQuantity") Integer minusQuantity) {
        return ResponseEntity.ok(eventService.updateEventTicketQuantity(id, minusQuantity));
    }

}
