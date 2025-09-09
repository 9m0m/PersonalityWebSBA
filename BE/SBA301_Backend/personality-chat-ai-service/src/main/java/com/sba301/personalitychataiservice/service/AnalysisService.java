package com.sba301.personalitychataiservice.service;

import com.sba301.personalitychataiservice.dto.AnalysisResultDTO;
public interface AnalysisService {
    AnalysisResultDTO analyzeConversation(String userId, String sessionId);
}
