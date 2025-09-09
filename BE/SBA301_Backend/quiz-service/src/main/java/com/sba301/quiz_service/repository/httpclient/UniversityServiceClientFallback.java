package com.sba301.quiz_service.repository.httpclient;

import com.sba301.quiz_service.dto.external.*;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class UniversityServiceClientFallback implements UniversityServiceClient {

    @Override
    public ApiResponse<List<UniversityRecommendationResponse>> getUniversitiesByPrograms(List<String> programs) {
        return ApiResponse.<List<UniversityRecommendationResponse>>builder()
                .code(503)
                .message("University service is currently unavailable")
                .result(new ArrayList<>())
                .build();
    }

    @Override
    public ApiResponse<List<UniversityRecommendationResponse>> searchUniversities(String major) {
        return ApiResponse.<List<UniversityRecommendationResponse>>builder()
                .code(503)
                .message("University service is currently unavailable")
                .result(new ArrayList<>())
                .build();
    }

    @Override
    public ApiResponse<List<UniversityRecommendationResponse>> getAllUniversities() {
        return ApiResponse.<List<UniversityRecommendationResponse>>builder()
                .code(503)
                .message("University service is currently unavailable")
                .result(new ArrayList<>())
                .build();
    }
}
