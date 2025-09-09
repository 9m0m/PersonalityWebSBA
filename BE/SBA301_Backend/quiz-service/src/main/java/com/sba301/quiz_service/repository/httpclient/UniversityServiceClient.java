package com.sba301.quiz_service.repository.httpclient;

import com.sba301.quiz_service.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.List;

@FeignClient(name = "university-service", fallback = UniversityServiceClientFallback.class)
public interface UniversityServiceClient {
    @GetMapping("/universities/by-programs")
    ApiResponse<List<UniversityRecommendationResponse>> getUniversitiesByPrograms(@RequestParam("programs") List<String> programs);

    @GetMapping("/universities/search")
    ApiResponse<List<UniversityRecommendationResponse>> searchUniversities(@RequestParam("major") String major);

    @GetMapping("/universities")
    ApiResponse<List<UniversityRecommendationResponse>> getAllUniversities();
}
