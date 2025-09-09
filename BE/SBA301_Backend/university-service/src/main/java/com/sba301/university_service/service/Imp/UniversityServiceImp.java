package com.sba301.university_service.service.Imp;

import com.sba301.university_service.dto.UniversityDTO;
import com.sba301.university_service.entity.University;
import com.sba301.university_service.repository.UniversityRepository;
import com.sba301.university_service.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityServiceImp implements UniversityService {
    @Autowired
    private UniversityRepository universityRepository;

    public University createUniversity(UniversityDTO dto) {
        University university = new University();
        updateUniversityFromDTO(university, dto);
        return universityRepository.save(university);
    }

    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public Optional<University> getUniversityById(String id) {
        return universityRepository.findById(id);
    }

    public University updateUniversity(String id, UniversityDTO dto) {
        return universityRepository.findById(id)
                .map(university -> {
                    updateUniversityFromDTO(university, dto);
                    return universityRepository.save(university);
                })
                .orElse(null);
    }

    public void deleteUniversity(String id) {
        universityRepository.deleteById(id);
    }

    // New methods to support quiz-service integration
    public List<University> findByMajorIn(List<String> majors) {
        return universityRepository.findByMajorIn(majors);
    }

    public List<University> findByMajor(String major) {
        return universityRepository.findByMajorContainingIgnoreCase(major);
    }

    private void updateUniversityFromDTO(University university, UniversityDTO dto) {
        university.setName(dto.getName());
        university.setMajor(dto.getMajor());
        university.setHotline(dto.getHotline());
        university.setLocation(dto.getLocation());
        university.setDescription(dto.getDescription());
        university.setPicture(dto.getPicture());
    }
}
