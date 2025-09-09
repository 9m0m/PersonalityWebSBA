package com.sba301.order_service.dto.response;

import lombok.*;


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
    // Additional fields can be added here if needed, such as item name, price, etc.
    private String itemName;
    private Double itemPrice;

}
