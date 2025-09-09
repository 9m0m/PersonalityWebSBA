package com.sba301.payment_service.entity;

import com.sba301.payment_service.entity.enumerate.PaymentMethod;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import vn.payos.type.CheckoutResponseData;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String checkoutUrl;

    @Column(nullable = false)
    private String qrCode;

    private String uid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    public static Transaction of(CheckoutResponseData checkoutResponseData){
        return Transaction.builder()
                .transactionId(Long.valueOf(checkoutResponseData.getOrderCode()))
                .orderId(null)
                .amount(Long.valueOf(checkoutResponseData.getAmount()))
                .time(LocalDateTime.now())
                .status(TransactionStatus.PENDING)
                .method(PaymentMethod.PAYOS)
                .description(checkoutResponseData.getDescription())
                .checkoutUrl(checkoutResponseData.getCheckoutUrl())
                .qrCode(checkoutResponseData.getQrCode())
                .build();
    }
}
