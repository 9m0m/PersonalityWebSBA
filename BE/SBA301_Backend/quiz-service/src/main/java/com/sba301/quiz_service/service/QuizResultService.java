package com.sba301.quiz_service.service;

import com.sba301.quiz_service.dto.PersonalityResultDTO;
import com.sba301.quiz_service.dto.QuizResultDTO;
import com.sba301.quiz_service.dto.QuizSubmissionDTO;
import com.sba301.quiz_service.dto.UserQuizResultsDTO;

public interface QuizResultService {
    PersonalityResultDTO submitQuizResultWithMicroservices(QuizSubmissionDTO submission, String userId);
    UserQuizResultsDTO getMyQuizResults(String userId, String authorizationHeader);
    UserQuizResultsDTO getUserResultByEmail(String email, String parentId);
    QuizResultDTO getResultById(Long resultId);
}
