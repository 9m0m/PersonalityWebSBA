package com.sba301.quiz_service.repository;

import com.sba301.quiz_service.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByQuizIdOrderByOrderNumber(Long quizId);

    List<QuizQuestion> findByDimension(String dimension);

    @Query("SELECT DISTINCT qq FROM QuizQuestion qq LEFT JOIN FETCH qq.options o WHERE qq.quizId = :quizId ORDER BY qq.orderNumber, o.id")
    List<QuizQuestion> findByQuizIdWithOptions(@Param("quizId") Long quizId);
}
