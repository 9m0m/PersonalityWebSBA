package com.sba301.quiz_service.service;

import com.sba301.quiz_service.dto.QuizDTO;
import com.sba301.quiz_service.dto.QuizRequestDTO;

import java.util.List;

public interface QuizService {
    List<QuizDTO> getAllQuiz();
    QuizDTO getQuizById(Long id);
    QuizDTO getQuizWithQuestions(Long id);
    List<QuizDTO> getQuizByCategory(Long categoryId);
    QuizDTO updateQuiz(Long id, QuizRequestDTO quizRequestDTO);
}