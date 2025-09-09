package com.sba301.quiz_service.controller;

import com.sba301.quiz_service.dto.PersonalityResultDTO;
import com.sba301.quiz_service.dto.QuizResultDTO;
import com.sba301.quiz_service.dto.QuizSubmissionDTO;
import com.sba301.quiz_service.dto.UserQuizResultsDTO;

import com.sba301.quiz_service.service.QuizResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz-results")
@RequiredArgsConstructor
@Slf4j
public class QuizResultController {

    private final QuizResultService quizResultService;

    //Submit quiz and get personality result
    @PostMapping("/submit")
    public ResponseEntity<PersonalityResultDTO> submitQuiz(
            @Valid @RequestBody QuizSubmissionDTO submission,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Received quiz submission request for user: {} and quiz: {}",
                userId, submission.getQuizId());

        PersonalityResultDTO result = quizResultService.submitQuizResultWithMicroservices(
                submission, userId);

        return ResponseEntity.ok(result);
    }

    //Get all quiz results for the authenticated user
    @GetMapping("/user/me")
    public ResponseEntity<UserQuizResultsDTO> getMyResults(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("Authorization") String authorizationHeader) { // Thêm header này
        log.info("Fetching quiz results for authenticated user");

        try {
            // Gọi phương thức service đã được cập nhật
            UserQuizResultsDTO results = quizResultService.getMyQuizResults(userId, authorizationHeader);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            log.error("Failed to get user results", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Get all quiz results for a user by their email address
    @GetMapping("/user/by-email")
    public ResponseEntity<UserQuizResultsDTO> getUserResultsByEmail(
            @RequestParam("email") String email,
            @RequestHeader("X-User-Id") String parentId) {
        log.info("Parent {} requesting quiz results for student {}", parentId, email);

        UserQuizResultsDTO results = quizResultService.getUserResultByEmail(email, parentId);
        return ResponseEntity.ok(results);
    }

    //Get specific quiz result by ID
    @GetMapping("/{resultId}")
    public ResponseEntity<QuizResultDTO> getResultById(@PathVariable Long resultId) {
        log.info("Fetching quiz result with ID: {}", resultId);

        QuizResultDTO result = quizResultService.getResultById(resultId);
        return ResponseEntity.ok(result);
    }

    //Exception handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error in QuizResultController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing the request: " + e.getMessage());
    }
}
