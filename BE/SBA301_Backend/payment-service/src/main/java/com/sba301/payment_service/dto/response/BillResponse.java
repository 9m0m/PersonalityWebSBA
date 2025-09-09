package com.sba301.payment_service.dto.response;

import com.sba301.payment_service.entity.Payment;
import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.PaymentMethod;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private String billId;
    private Long orderId;
    private Long amount;
    private LocalDateTime time;
    private String description;
    private String method;
    private String status;

    public static BillResponse fromTransaction(Payment payment) {
        return BillResponse.builder()
                .billId(payment.getId())
                .orderId(payment.getTransaction().getOrderId())
                .amount(payment.getAmount())
                .time(payment.getTransaction().getTime())
                .description(payment.getTransaction().getDescription())
                .method(payment.getTransaction().getMethod() != null ? payment.getTransaction().getMethod().name() : null)
                .build();
    }
}
