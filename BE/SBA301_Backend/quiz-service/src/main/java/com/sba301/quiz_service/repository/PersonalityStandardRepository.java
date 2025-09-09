package com.sba301.quiz_service.repository;

import com.sba301.quiz_service.entity.PersonalityStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalityStandardRepository extends JpaRepository<PersonalityStandard, Long> {

    Optional<PersonalityStandard> findByPersonalityCode(String personalityCode);

    List<PersonalityStandard> findByStandardOrderByPersonalityCode(PersonalityStandard.StandardType standard);
}
