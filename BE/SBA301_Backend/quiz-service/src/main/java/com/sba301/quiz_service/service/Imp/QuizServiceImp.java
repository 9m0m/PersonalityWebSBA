package com.sba301.quiz_service.service.Imp;

import com.sba301.quiz_service.dto.QuizDTO;
import com.sba301.quiz_service.dto.QuizRequestDTO;
import com.sba301.quiz_service.entity.Quiz;
import com.sba301.quiz_service.exception.QuizNotFoundException;
import com.sba301.quiz_service.repository.QuizRepository;
import com.sba301.quiz_service.entity.Category;
import com.sba301.quiz_service.exception.CategoryNotFoundException;
import com.sba301.quiz_service.repository.CategoryRepository;
import com.sba301.quiz_service.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImp implements QuizService {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuiz() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));
        return convertToDTO(quiz);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizWithQuestions(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));
        QuizDTO quizDTO = convertToDTO(quiz);
        return quizDTO;
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        List<Quiz> quizzes = quizRepository.findByCategoryId(categoryId);
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuizDTO updateQuiz(Long id, QuizRequestDTO quizRequestDTO) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        Category category = categoryRepository.findById(quizRequestDTO.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(quizRequestDTO.getCategoryId()));

        existingQuiz.setTitle(quizRequestDTO.getTitle());
        existingQuiz.setCategory(category);
        existingQuiz.setDescription(quizRequestDTO.getDescription());
        existingQuiz.setQuestionQuantity(quizRequestDTO.getQuestionQuantity());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return convertToDTO(updatedQuiz);
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .categoryId(quiz.getCategory().getId())
                .categoryName(quiz.getCategory().getName())
                .description(quiz.getDescription())
                .questionQuantity(quiz.getQuestionQuantity())
                .build();
    }
}

