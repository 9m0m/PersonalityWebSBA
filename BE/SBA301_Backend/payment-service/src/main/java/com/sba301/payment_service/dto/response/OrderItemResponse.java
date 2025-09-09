package com.sba301.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemResponse {
    private Integer id;
    private Integer itemId;
    private Integer quantity;
    private String itemType;
    private String itemName;
    private Double itemPrice;
}
