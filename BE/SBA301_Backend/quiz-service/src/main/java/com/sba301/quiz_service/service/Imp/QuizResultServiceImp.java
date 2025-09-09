package com.sba301.quiz_service.service.Imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba301.quiz_service.dto.PersonalityResultDTO;
import com.sba301.quiz_service.dto.QuizResultDTO;
import com.sba301.quiz_service.dto.QuizSubmissionDTO;
import com.sba301.quiz_service.dto.UserQuizResultsDTO;
import com.sba301.quiz_service.dto.external.*;
import com.sba301.quiz_service.entity.QuizResult;
import com.sba301.quiz_service.entity.PersonalityStandard;
import com.sba301.quiz_service.exception.QuizNotFoundException;
import com.sba301.quiz_service.repository.QuizResultRepository;
import com.sba301.quiz_service.repository.PersonalityStandardRepository;
import com.sba301.quiz_service.repository.httpclient.AuthServiceClient;

import com.sba301.quiz_service.service.QuizResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizResultServiceImp implements QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final PersonalityStandardRepository personalityStandardRepository;
    private final PersonalityCalculationServiceImp personalityCalculationService;
    private final MicroserviceIntegrationServiceImp microserviceIntegrationService;
    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    public PersonalityResultDTO submitQuizResultWithMicroservices(QuizSubmissionDTO submission, String userId) {
        log.info("Processing quiz submission with microservices integration for user: {} and quiz: {}",
                userId, submission.getQuizId());

        try {
            submission.setUserId(userId);

            PersonalityResultDTO personalityResult = personalityCalculationService.calculatePersonality(submission);
            log.info("Calculated personality type: {} for user: {}", personalityResult.getPersonalityCode(), userId);

            PersonalityResultDTO enrichedResult = microserviceIntegrationService.enrichPersonalityResult(personalityResult);
            log.info("Enriched personality result with microservices data");

            QuizResult quizResult = saveQuizResult(submission, enrichedResult);
            log.info("Saved quiz result with ID: {}", quizResult.getId());

            return enrichedResult;

        } catch (Exception e) {
            log.error("Failed to process quiz submission with microservices for user: {}", userId, e);
            throw new RuntimeException("Failed to submit quiz: " + e.getMessage());
        }
    }

    private QuizResult saveQuizResult(QuizSubmissionDTO submission, PersonalityResultDTO personalityResult) {
        try {
            String personalityJson = objectMapper.writeValueAsString(personalityResult);

            QuizResult quizResult = new QuizResult();
            quizResult.setUserId(submission.getUserId());
            quizResult.setQuizId(submission.getQuizId());
            quizResult.setResultType(personalityResult.getPersonalityCode());
            quizResult.setResultJson(personalityJson);
            quizResult.setTimeSubmit(LocalDateTime.now());
            quizResult.setAttemptOrder(getNextAttemptOrder(submission.getUserId(), submission.getQuizId()));

            Optional<PersonalityStandard> personalityStandard = personalityStandardRepository
                    .findByPersonalityCode(personalityResult.getPersonalityCode());
            personalityStandard.ifPresent(standard -> quizResult.setPersonalityId(standard.getId()));

            return quizResultRepository.save(quizResult);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize personality result to JSON", e);
            throw new RuntimeException("Failed to save quiz result");
        }
    }

    private Integer getNextAttemptOrder(String userId, Long quizId) {
        return getQuizAttemptCount(userId, quizId) + 1;
    }

    public UserQuizResultsDTO getMyQuizResults(String userId, String authorizationHeader) { // Thêm authorizationHeader
        try {
            log.info("Fetching user details for authenticated user: {}", userId);
            //Gọi auth-service để lấy thông tin chi tiết của người dùng (tên, email,...)
            ApiResponse<UserResponse> userResponse = authServiceClient.getUserById(userId, authorizationHeader);
            UserResponse currentUser = userResponse.getResult();

            if (currentUser == null) {
                log.error("Could not fetch user details for user ID: {}", userId);
                throw new RuntimeException("User not found with ID: " + userId);
            }

            log.info("Fetching quiz results for authenticated user: {}", userId);
            //Lấy danh sách kết quả bài trắc nghiệm từ DB
            List<QuizResult> results = quizResultRepository.findByUserIdWithPersonalityDetails(userId);

            //Xây dựng DTO trả về với thông tin người dùng đã được điền đầy đủ
            return buildUserQuizResultsDTO(currentUser, results); // Truyền đối tượng currentUser vào

        } catch (Exception e) {
            log.error("Failed to get user results for authenticated user", e);
            throw new RuntimeException("Failed to fetch user results", e);
        }
    }

    @Transactional(readOnly = true)
    public QuizResultDTO getResultById(Long id) {
        log.debug("Fetching quiz result with ID: {}", id);

        QuizResult result = quizResultRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz result not found with id: " + id));
        return convertToDTO(result);
    }

    @Transactional(readOnly = true)
    public List<QuizResultDTO> getResultsByQuizAndUser(Long quizId, String userId) {
        log.debug("Fetching quiz results for quiz: {} and user: {}", quizId, userId);

        List<QuizResult> results = quizResultRepository.findByQuizIdAndUserId(quizId, userId);
        return results.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getQuizAttemptCount(String userId, Long quizId) {
        return quizResultRepository.countByQuizIdAndUserId(quizId, userId);
    }

    private QuizResultDTO convertToDTO(QuizResult result) {
        QuizResultDTO dto = new QuizResultDTO();
        dto.setId(result.getId());
        dto.setResultType(result.getResultType());
        dto.setTimeSubmit(result.getTimeSubmit());
        dto.setAttemptOrder(result.getAttemptOrder());
        dto.setResultJson(result.getResultJson());
        dto.setQuizId(result.getQuizId());
        dto.setUserId(result.getUserId());
        dto.setPersonalityId(result.getPersonalityId());
        return dto;
    }

    public UserQuizResultsDTO getUserResultByEmail(String email, String parentId) {
        log.info("Executing request for parent {} to get results of student {}", parentId, email);

        try {
            ApiResponse<UserResponse> studentResponse = authServiceClient.getUserByEmail(email, null);
            UserResponse student = studentResponse.getResult();

            if (student == null) {
                log.warn("No student found with email: {}", email);
                return UserQuizResultsDTO.builder()
                        .email(email)
                        .totalQuizzesTaken(0)
                        .quizResults(new ArrayList<>())
                        .build();
            }
            //Get student's quiz results
            List<QuizResult> quizResults = quizResultRepository.findByUserIdWithPersonalityDetails(student.getId());
            log.info("Successfully retrieved {} quiz results for student: {}", quizResults.size(), email);

            return buildUserQuizResultsDTO(student, quizResults);

        } catch (Exception e) {
            log.error("Failed to get quiz results for student with email: {}", email, e);
            throw new RuntimeException("Failed to retrieve student quiz results: " + e.getMessage());
        }
    }

    private UserQuizResultsDTO buildUserQuizResultsDTO(UserResponse user, List<QuizResult> quizResults) {
        List<UserQuizResultsDTO.QuizResultSummaryDTO> resultSummaries = quizResults.stream()
                .map(this::convertToResultSummary)
                .collect(Collectors.toList());

        LocalDateTime firstQuizDate = quizResults.stream()
                .map(QuizResult::getTimeSubmit)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastQuizDate = quizResults.stream()
                .map(QuizResult::getTimeSubmit)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return UserQuizResultsDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .totalQuizzesTaken(quizResults.size())
                .firstQuizDate(firstQuizDate)
                .lastQuizDate(lastQuizDate)
                .quizResults(resultSummaries)
                .build();
    }

    private UserQuizResultsDTO.QuizResultSummaryDTO convertToResultSummary(QuizResult quizResult) {
        return UserQuizResultsDTO.QuizResultSummaryDTO.builder()
                .resultId(quizResult.getId())
                .quizId(quizResult.getQuizId())
                .quizTitle(quizResult.getQuiz() != null ? quizResult.getQuiz().getTitle() : "Unknown Quiz")
                .resultType(quizResult.getResultType())
                .personalityCode(quizResult.getPersonalityStandard() != null ?
                        quizResult.getPersonalityStandard().getPersonalityCode() : null)
                .personalityName(quizResult.getPersonalityStandard() != null ?
                        quizResult.getPersonalityStandard().getNickname() : null)
                .personalityDescription(quizResult.getPersonalityStandard() != null ?
                        quizResult.getPersonalityStandard().getDescription() : null)
                .attemptOrder(quizResult.getAttemptOrder())
                .timeSubmit(quizResult.getTimeSubmit())
                .resultJson(quizResult.getResultJson())
                .build();
    }
}
