package com.sba301.payment_service.dto.response;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponse {
    private Integer id;

    private LocalDateTime time;

    private Double total;

    private String status;

    private String userId;

    private List<OrderItemResponse> items;
}
