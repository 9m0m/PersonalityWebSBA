package com.sba301.university_service.repository;

import com.sba301.university_service.entity.University;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends MongoRepository<University, String> {
    List<University> findByMajorContainingIgnoreCase(String major);
    List<University> findByMajorIn(List<String> majors);
}
