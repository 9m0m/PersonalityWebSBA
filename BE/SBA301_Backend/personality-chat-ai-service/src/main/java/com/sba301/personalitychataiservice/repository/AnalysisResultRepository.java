package com.sba301.personalitychataiservice.repository;

import com.sba301.personalitychataiservice.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, UUID> {
    boolean existsBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    Optional<AnalysisResult> findByUserIdAndSessionId(String userId, String sessionId);
}