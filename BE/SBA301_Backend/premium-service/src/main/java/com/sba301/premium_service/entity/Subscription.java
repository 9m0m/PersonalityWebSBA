package com.sba301.premium_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Subscription {
    public enum SubscriptionStatus {
        ACTIVE, CANCELLED, EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String uid;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "premium_id", referencedColumnName = "id")
    private Premium premium;
}
