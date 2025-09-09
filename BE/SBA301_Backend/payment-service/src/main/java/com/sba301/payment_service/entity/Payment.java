package com.sba301.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "Payment")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @Column(name = "paymentId", nullable = false, unique = true)
    private String id;

    private Long amount;

    private Long fee;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id")
    private Transaction transaction;
}
