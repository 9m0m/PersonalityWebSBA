package com.sba301.event_service.repository;

import com.sba301.event_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event>, PagingAndSortingRepository<Event, Integer> {
    Optional<Event> findById(int id);
    Optional<Event> findBySlug(String slug);

    Optional<List<Event>> findByStatus(Event.EventStatus status);

}
