package com.sba301.quiz_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private String userId;

    @NotEmpty(message = "Answers are required")
    private Map<Long, Long> answers;

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
