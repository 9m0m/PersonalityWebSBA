package com.sba301.quiz_service.exception;

public class QuizAttemptLimitExceededException extends RuntimeException {
    public QuizAttemptLimitExceededException(String message) {
        super(message);
    }

    public QuizAttemptLimitExceededException(Long userId, Long quizId) {
        super("Quiz attempt limit exceeded for user: " + userId + " and quiz: " + quizId);
    }
}
