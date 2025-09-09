package com.sba301.career_service.service;

import com.sba301.career_service.dto.CareerDTO;
import com.sba301.career_service.entity.Career;

import java.util.List;
import java.util.Optional;

public interface CareerService {
    Career createCareer(CareerDTO dto);
    List<Career> getAllCareers();
    Optional<Career> getCareerById(String id);
    Career updateCareer(String id, CareerDTO dto);
    void deleteCareer(String id);
    List<Career> getCareersByPersonality(String personalityType);
    List<Career> searchCareersByPersonalityTypes(List<String> personalityTypes);
    List<Career> searchCareersByNames(List<String> careerNames);
}
