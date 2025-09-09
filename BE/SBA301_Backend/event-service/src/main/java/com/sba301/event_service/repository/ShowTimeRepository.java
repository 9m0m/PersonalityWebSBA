package com.sba301.event_service.repository;

import com.sba301.event_service.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Integer> {
    // Additional query methods can be defined here if needed
}
