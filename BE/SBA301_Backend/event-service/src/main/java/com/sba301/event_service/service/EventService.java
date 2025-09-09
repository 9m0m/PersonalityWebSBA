package com.sba301.event_service.service;

import com.sba301.event_service.config.AuthContext;
import com.sba301.event_service.dto.*;
import com.sba301.event_service.entity.Event;
import com.sba301.event_service.entity.ShowTime;
import com.sba301.event_service.entity.Ticket;
import com.sba301.event_service.exception.ResourceNotFoundException;
import com.sba301.event_service.mapper.EventMapper;
import com.sba301.event_service.mapper.ShowTimeMapper;
import com.sba301.event_service.mapper.TicketMapper;
import com.sba301.event_service.repository.EventRepository;
import com.sba301.event_service.repository.ShowTimeRepository;
import com.sba301.event_service.repository.TicketRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventService implements IEventService {
    private final EventRepository eventRepository;
    private final AuthContext authContext;
    private final EventMapper eventMapper;
    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeMapper showTimeMapper;
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;

    public Page<EventOverviewResponse> getEvents(
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
    ) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (organizerId != null && (authContext.hasRole("ROLE_ADMIN") || authContext.getUserId().equals(organizerId))) {
                predicates.add(cb.equal(root.get("organizerId"), organizerId));
            }
            if (moderatorId != null && authContext.hasRole("ROLE_ADMIN")) {
                predicates.add(cb.equal(root.get("moderatorId"), moderatorId));
            }
            if (status != null) {
                try {
                    Event.EventStatus enumStatus = Event.EventStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), enumStatus));
                    // PENDING, REJECTED, CANCELLED – Admin or organizer only
                    if (enumStatus == Event.EventStatus.PENDING
                            || enumStatus == Event.EventStatus.REJECTED
                            || enumStatus == Event.EventStatus.CANCELLED) {
                        boolean isAdmin = authContext.hasRole("ROLE_ADMIN");
                        boolean isOrganizer = organizerId != null && authContext.getUserId().equals(organizerId);
                        if (isAdmin || isOrganizer) {
                            predicates.add(cb.equal(root.get("status"), enumStatus));
                        } else {
                            throw new AccessDeniedException("You are not allowed to view events with this status.");
                        }
                    }
                } catch (ResourceNotFoundException e) {
                    throw new ResourceNotFoundException("Invalid status value: " + status);
                }
            } else {
                predicates.add(cb.equal(root.get("isApproved"), true));
                if (name != null) {
                    predicates.add(root.get("status").in(
                            Event.EventStatus.UPCOMING,
                            Event.EventStatus.ONGOING,
                            Event.EventStatus.COMPLETED)
                    );
                } else {
                    predicates.add(root.get("status").in(
                            Event.EventStatus.UPCOMING,
                            Event.EventStatus.ONGOING)
                    );
                }

            }

            if (personalityTypes != null && !personalityTypes.isEmpty()) {
                predicates.add(root.get("personalityTypes").in(personalityTypes.split(",")));
            }

            Join<Event, ShowTime> showTime = root.join("showtimes", JoinType.INNER);
            if (from != null && to != null) {
                predicates.add(cb.between(showTime.get("startTime"), from.atStartOfDay(), to.atTime(23, 59, 59)));
            } else if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(showTime.get("startTime"), from.atStartOfDay()));
            } else if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(showTime.get("startTime"), to.atTime(23, 59, 59)));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<String> allowedSortFields = List.of("id", "name", "createdAt", "updateAt");
        if (sortBy == null || !allowedSortFields.contains(sortBy)) {
            sortBy = "id";
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (ResourceNotFoundException | NullPointerException e) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return eventRepository.findAll(spec, pageable).map(eventMapper::toEventOverviewResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EVENT_MANAGER')")
    @PostAuthorize("hasRole('ADMIN') or returnObject.organizerId() == authentication.principal.claims['sub']")
    public EventPrivateDetailResponse getEventById(int id) {

        var event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        return eventMapper.toEventPrivateDetailResponse(event);
    }

    public EventPublicDetailResponse getEventBySlug(String slug) {
        var event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));

        // upcoming, ongoing, completed
        if (event.isApproved() && event.getStatus() != Event.EventStatus.PENDING && event.getStatus() != Event.EventStatus.REJECTED && event.getStatus() != Event.EventStatus.CANCELLED) {

            List<ShowTimeResponse> showTimeResponses = new ArrayList<>();
            for (ShowTime showTime : event.getShowtimes()) {
                LocalDateTime now = LocalDateTime.now();

                // ongoing showtime && user bought ticket for this showtime
                if (showTime.getStartTime().isBefore(now)
                        && showTime.getEndTime().isAfter(now)) {

                    // order api to check
                    showTimeResponses.add(showTimeMapper.toShowTimeResponseWithMeeting(showTime));

                } else {
                    showTimeResponses.add(showTimeMapper.toShowTimeResponse(showTime));
                }
            }

            return EventPublicDetailResponse.builder()
                    .id(event.getId())
                    .slug(event.getSlug())
                    .name(event.getName())
                    .description(event.getDescription())
                    .bannerUrl(event.getBannerUrl())
                    .organizerId(event.getOrganizerId())
                    .status(event.getStatus())
                    .personalityTypes(event.getPersonalityTypes())
                    .showtimes(showTimeResponses)
                    .build();
        }



        throw new ResourceNotFoundException("Event not found with slug: " + slug);
    }

    @Transactional
    public void createDraftEvent(EventCreateRequest eventCreateRequest) {
        var event = eventMapper.toEvent(eventCreateRequest);



        if (event.getPersonalityTypes() != null && event.getPersonalityTypes().isEmpty()) {
            event.setPersonalityTypes(event.getPersonalityTypes().replace("\\s+", "-").toLowerCase());
        }

        if (event.getShowtimes() == null || event.getShowtimes().isEmpty()) {
            throw new ResourceNotFoundException("Event must have at least one showtime.");
        }

        for (ShowTime showTime : event.getShowtimes()) {
            showTime.setEvent(event);
            for (Ticket ticket : showTime.getTickets()) {
                ticket.setShowTime(showTime);
                ticket.setStatus(Ticket.TicketStatus.ACTIVE);
            }
        }
        event.setCreatedAt(java.time.LocalDateTime.now());
        event.setOrganizerId(authContext.getUserId());
        event.setStatus(Event.EventStatus.DRAFT);
        event.setApproved(false);
        eventRepository.save(event);
    }

    @Transactional
    public URI createAndSubmitEvent(EventCreateRequest eventCreateRequest) {
        var event = eventMapper.toEvent(eventCreateRequest);

        if (eventRepository.findBySlug(event.getSlug()).isPresent()) {
            throw new IllegalArgumentException("Event with slug '" + event.getSlug() + "' already exists.");
        }

        if (event.getPersonalityTypes() != null && event.getPersonalityTypes().isEmpty()) {
            event.setPersonalityTypes(event.getPersonalityTypes().replace("\\s+", "-").toLowerCase());
        }

        for (ShowTime showTime : event.getShowtimes()) {
            showTime.setEvent(event);
            for (Ticket ticket : showTime.getTickets()) {
                ticket.setShowTime(showTime);
                ticket.setStatus(Ticket.TicketStatus.ACTIVE);
            }
        }
        event.setCreatedAt(java.time.LocalDateTime.now());
        event.setOrganizerId(authContext.getUserId());
        event.setStatus(Event.EventStatus.PENDING);
        event.setApproved(false);
        eventRepository.save(event);
        return URI.create("/events/" + event.getSlug());
    }

    @Transactional
    public EventSubmissionResponse submitDraftEvent(int id, EventUpdateRequest eventUpdateRequest) {
        var existingEvent = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
        var event = eventMapper.toEvent(eventUpdateRequest, existingEvent);
        event.setStatus(Event.EventStatus.PENDING);
        return eventMapper.toEventSubmissionResponse(eventRepository.save(event));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public EventPrivateDetailResponse approveEvent(int id, EventApproveRequest eventApproveRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventApproveRequest.id()));

        event.setApproved(true);
        event.setNotes(eventApproveRequest.notes());
        event.setStatus(Event.EventStatus.UPCOMING);

        for (ShowTime showTime: event.getShowtimes()) {
            showTime.setMeetingId(UUID.randomUUID().toString());
            showTime.setMeetingPassword(UUID.randomUUID().toString());
            showTimeRepository.save(showTime);
        }

        event.setModeratorId(authContext.getUserId());
        event.setUpdatedAt(java.time.LocalDateTime.now());
        return eventMapper.toEventPrivateDetailResponse(eventRepository.save(event));
    }

    public EventPrivateDetailResponse updateDraftEvent(int id, EventUpdateRequest eventUpdateRequest) {
        var existingEvent = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        return eventMapper.toEventPrivateDetailResponse(
                eventRepository.save(
                        eventMapper.toEvent(eventUpdateRequest, existingEvent)));
    }

    public EventPrivateDetailResponse rejectEvent(int id, EventApproveRequest eventApproveRequest) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventApproveRequest.id()));
        event.setNotes(eventApproveRequest.notes());
        event.setStatus(Event.EventStatus.REJECTED);
        event.setModeratorId(authContext.getUserId());
        event.setUpdatedAt(java.time.LocalDateTime.now());
        return eventMapper.toEventPrivateDetailResponse(eventRepository.save(event));
    }

    public EventPrivateDetailResponse cancelEvent(int id, EventApproveRequest eventApproveRequest) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventApproveRequest.id()));
        event.setNotes(eventApproveRequest.notes());
        event.setStatus(Event.EventStatus.CANCELLED);
        event.setModeratorId(authContext.getUserId());
        event.setUpdatedAt(java.time.LocalDateTime.now());
        return eventMapper.toEventPrivateDetailResponse(eventRepository.save(event));
    }

    public EventPrivateDetailResponse updateEvent(int id, EventUpdateRequest eventUpdateRequest) {
        var existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        if (eventUpdateRequest.personalityTypes() != null && eventUpdateRequest.personalityTypes().isEmpty()) {
            existingEvent.setPersonalityTypes(eventUpdateRequest.personalityTypes().replace("\\s+", "-").toLowerCase());
        }

        existingEvent = eventMapper.toEvent(eventUpdateRequest, existingEvent);
        existingEvent.setUpdatedAt(java.time.LocalDateTime.now());
        return eventMapper.toEventPrivateDetailResponse(eventRepository.save(existingEvent));
    }

    @Scheduled(fixedRate = 1 * 60 * 1000)
    @Transactional
    public void updateEventStatus() {
        log.info("Running scheduler...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in10Min = now.plusMinutes(10);

        updateUpcomingEvents(in10Min);
        updateOngoingEvents(now);
    }

    private void updateUpcomingEvents(LocalDateTime in10Min) {
        List<Event> upcomingEvents = eventRepository.findByStatus(Event.EventStatus.UPCOMING)
                .orElseThrow(() -> new ResourceNotFoundException("No upcoming events found"));

        for (Event event : upcomingEvents) {
            boolean shouldBeOngoing = event.getShowtimes().stream()
                    .anyMatch(showTime -> !showTime.getStartTime().isAfter(in10Min));

            if (shouldBeOngoing) {
                event.setStatus(Event.EventStatus.ONGOING);
                eventRepository.save(event);
            }
        }
    }

    private void updateOngoingEvents(LocalDateTime now) {
        List<Event> ongoingEvents = eventRepository.findByStatus(Event.EventStatus.ONGOING)
                .orElseThrow(() -> new ResourceNotFoundException("No ongoing events found"));

        for (Event event : ongoingEvents) {
            boolean allEnded = event.getShowtimes().stream()
                    .allMatch(showTime -> showTime.getEndTime().isBefore(now));

            if (allEnded) {
                event.setStatus(Event.EventStatus.COMPLETED);
                eventRepository.save(event);
            }
        }
    }


    public TicketResponse getTicketById(int id) {
        var ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + id));

        return ticketMapper.toTicketResponse(ticket);
    }

    public List<TicketResponse> getTicketsByShowTimeId(int showTimeId) {
        var showTime = showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("ShowTime not found with ID: " + showTimeId));

        return showTime.getTickets().stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }

    public TicketResponse updateEventTicketQuantity(int id, int minusQuantity) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + id));

        //check quantity validity
        if (ticket.getQuantity() < minusQuantity) {
            String alertMessage = "Not enough tickets available for itemId: " + ticket.getId() + ", remaining: " + (ticket != null ? ticket.getQuantity() : 0);
            throw new IllegalArgumentException(alertMessage);
        }

        int newQuantity = ticket.getQuantity() - minusQuantity;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        ticket.setQuantity(newQuantity);
        ticketRepository.save(ticket);
        return ticketMapper.toTicketResponse(ticket);

    }
}
