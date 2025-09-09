package com.sba301.quiz_service.repository;

import com.sba301.quiz_service.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByQuizIdAndUserId(Long quizId, String userId);

    Integer countByQuizIdAndUserId(Long quizId, String userId);

    @Query("SELECT qr FROM QuizResult qr LEFT JOIN FETCH qr.personalityStandard WHERE qr.userId = :userId ORDER BY qr.timeSubmit DESC")
    List<QuizResult> findByUserIdWithPersonalityDetails(@Param("userId") String userId);
}
