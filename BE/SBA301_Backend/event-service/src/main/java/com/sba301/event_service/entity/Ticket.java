package com.sba301.event_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Ticket {
    public enum TicketStatus {
        INACTIVE,
        ACTIVE,
        FULFILLED,
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private int price;
    private int quantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    private ShowTime showTime;
}
