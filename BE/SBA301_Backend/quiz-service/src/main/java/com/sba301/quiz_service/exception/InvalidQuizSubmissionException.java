package com.sba301.quiz_service.exception;

public class InvalidQuizSubmissionException extends RuntimeException {
    public InvalidQuizSubmissionException(String message) {
        super(message);
    }
}
