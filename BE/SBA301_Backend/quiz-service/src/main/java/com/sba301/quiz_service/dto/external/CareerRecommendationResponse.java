package com.sba301.quiz_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerRecommendationResponse {
    private String id;
    private String name;
    private String description;
}
