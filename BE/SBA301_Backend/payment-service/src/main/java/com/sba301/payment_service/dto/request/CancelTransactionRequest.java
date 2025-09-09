package com.sba301.payment_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CancelTransactionRequest {
    @NotNull(message = "Cancellation Reason must not be empty")
    String cancellationReason;
}
