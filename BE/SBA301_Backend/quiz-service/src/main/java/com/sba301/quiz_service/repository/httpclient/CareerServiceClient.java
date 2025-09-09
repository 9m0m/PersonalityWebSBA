package com.sba301.quiz_service.repository.httpclient;

import com.sba301.quiz_service.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.List;

@FeignClient(name = "career-service", fallback = CareerServiceClientFallback.class)
public interface CareerServiceClient {
    @GetMapping(value = "/careers/search", params = "careerNames", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<CareerRecommendationResponse>> searchCareersByName(@RequestParam("careerNames") List<String> careerNames);

    @GetMapping(value = "/careers/personality/{personalityType}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<CareerRecommendationResponse>> getCareersByPersonality(@PathVariable("personalityType") String personalityType);

    @GetMapping("/careers")
    ApiResponse<List<CareerRecommendationResponse>> getAllCareers();
}
