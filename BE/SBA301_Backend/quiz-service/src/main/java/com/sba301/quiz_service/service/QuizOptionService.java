package com.sba301.quiz_service.service;

import com.sba301.quiz_service.dto.QuizOptionsDTO;
import com.sba301.quiz_service.entity.QuizOptions;

import java.util.List;
import java.util.Map;

public interface QuizOptionService {
    List<QuizOptionsDTO> getOptionsByQuestionId(Long questionId);
    List<QuizOptionsDTO> getOptionsByQuestionIds(List<Long> questionIds);
    QuizOptionsDTO getOptionById(Long id);
    List<QuizOptionsDTO> getOptionsByTargetTrait(String targetTrait);
    List<QuizOptionsDTO> getOptionsByScoreValue(QuizOptions.ScoreValue scoreValue);
    List<QuizOptionsDTO> getOptionsGroupedByTargetTrait(Long questionId, String targetTrait);
    QuizOptionsDTO createOption(QuizOptionsDTO optionDTO);
    List<QuizOptionsDTO> createOptions(List<QuizOptionsDTO> optionDTOs);
    QuizOptionsDTO updateOption(Long id, QuizOptionsDTO optionDTO);
    void deleteOptionsByQuestionId(Long questionId);
    long countOptionsByQuestionId(Long questionId);
    boolean optionExists(Long id);
}