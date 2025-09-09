package com.sba301.quiz_service.service;

import com.sba301.quiz_service.dto.QuizQuestionDTO;

import java.util.List;

public interface QuizQuestionService {
    List<QuizQuestionDTO> getQuestionsByQuizId(Long quizId);
    QuizQuestionDTO getQuestionById(Long id);
    List<QuizQuestionDTO> getQuestionsByDimension(String dimension);
    QuizQuestionDTO createQuestion(QuizQuestionDTO questionDTO);
    QuizQuestionDTO updateQuestion(Long id, QuizQuestionDTO questionDTO);
    void deleteQuestion(Long id);
}