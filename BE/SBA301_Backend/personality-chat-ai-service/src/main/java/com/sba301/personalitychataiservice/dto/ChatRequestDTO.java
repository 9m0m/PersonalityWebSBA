package com.sba301.personalitychataiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDTO {
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    @NotBlank(message = "Message cannot be empty")
    private String message;

    private boolean requestAnalysis = false;

    public static ChatRequestDTO of(String sessionId, String message) {
        return ChatRequestDTO.builder()
                .sessionId(sessionId)
                .message(message)
                .build();
    }
}
