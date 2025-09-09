package com.sba301.event_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    public enum EventStatus {
        DRAFT,
        PENDING,
        UPCOMING,
        ONGOING,
        COMPLETED,
        CANCELLED,
        REJECTED,
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    @Column(unique = true)
    private String slug;
    private String bannerUrl;

    private String organizerId;
    private LocalDateTime createdAt;
    private String moderatorId;
    private LocalDateTime updatedAt;
    private String notes;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    private boolean isApproved;
    private String changes;

    private String personalityTypes;

    private String bankAccountName;
    private String bankAccountNumber;
    private String bankName;
    private String bankBranch;

    private String vatBusinessType;
    private String vatHolderName;
    private String vatHolderAddress;
    private String taxCode;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowTime> showtimes;
}
