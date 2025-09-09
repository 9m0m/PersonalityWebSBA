package com.sba301.quiz_service.service;

import com.sba301.quiz_service.dto.PersonalityStandardDTO;
import com.sba301.quiz_service.entity.PersonalityStandard;

import java.util.List;

public interface PersonalityStandardService {
    List<PersonalityStandardDTO> getAllPersonalityStandards();

    PersonalityStandardDTO getPersonalityStandardById(Long id);

    List<PersonalityStandardDTO> getByStandard(PersonalityStandard.StandardType standard);

    PersonalityStandardDTO getByPersonalityCode(String personalityCode);

    PersonalityStandardDTO updatePersonalityStandard(Long id, PersonalityStandardDTO dto);
}
