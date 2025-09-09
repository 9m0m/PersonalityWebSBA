package com.sba301.order_service.dto.response;

import com.sba301.order_service.entity.Order;
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

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .time(order.getTime())
                .total(order.getTotal())
                .status(order.getStatus().name())
                .userId(order.getUserId())
//                .items(order.getItems().stream()
//                        .map(OrderItemResponse::of)
//                        .toList())
                .build();
    }
}
