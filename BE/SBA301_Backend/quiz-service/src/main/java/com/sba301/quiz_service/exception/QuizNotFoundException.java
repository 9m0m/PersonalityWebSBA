package com.sba301.quiz_service.exception;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(String message) {
        super(message);
    }

    public QuizNotFoundException(Long id) {
        super("Quiz not found with id: " + id);
    }
}