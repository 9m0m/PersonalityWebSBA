package com.sba301.order_service.dto.request;

import com.sba301.order_service.entity.enums.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
    @NotNull(message = "Item ID must not be empty")
    Integer itemId;

    @NotNull(message = "Item type must not be empty")
    ItemType itemType;

    @NotNull(message = "Quantity must not be empty")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;
}
