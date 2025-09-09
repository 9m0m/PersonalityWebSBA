package com.sba301.quiz_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private Long id;
    private String resultType;
    private LocalDateTime timeSubmit;
    private Integer attemptOrder;
    private String resultJson;
    private Long quizId;
    private String userId;
    private Long personalityId;
}
