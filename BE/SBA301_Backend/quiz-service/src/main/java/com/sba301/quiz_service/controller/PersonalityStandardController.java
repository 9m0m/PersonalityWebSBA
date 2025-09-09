package com.sba301.quiz_service.controller;

import com.sba301.quiz_service.dto.PersonalityStandardDTO;
import com.sba301.quiz_service.entity.PersonalityStandard;
import com.sba301.quiz_service.service.PersonalityStandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/personality-standards")
@RequiredArgsConstructor
public class PersonalityStandardController {

    private final PersonalityStandardService personalityStandardService;

    @GetMapping
    public ResponseEntity<List<PersonalityStandardDTO>> getAllPersonalityStandards() {
        List<PersonalityStandardDTO> standards = personalityStandardService.getAllPersonalityStandards();
        return ResponseEntity.ok(standards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalityStandardDTO> getPersonalityStandardById(
            @PathVariable Long id) {
        PersonalityStandardDTO standard = personalityStandardService.getPersonalityStandardById(id);
        return ResponseEntity.ok(standard);
    }

    @GetMapping("/standard/{standardType}")
    public ResponseEntity<List<PersonalityStandardDTO>> getByStandard(
            @PathVariable PersonalityStandard.StandardType standardType) {
        List<PersonalityStandardDTO> standards = personalityStandardService.getByStandard(standardType);
        return ResponseEntity.ok(standards);
    }

    @GetMapping("/code/{personalityCode}")
    public ResponseEntity<PersonalityStandardDTO> getByPersonalityCode(
            @PathVariable String personalityCode) {
        PersonalityStandardDTO standard = personalityStandardService.getByPersonalityCode(personalityCode);
        return ResponseEntity.ok(standard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalityStandardDTO> updatePersonalityStandard(
            @PathVariable Long id,
            @Valid @RequestBody PersonalityStandardDTO personalityStandardDTO) {
        PersonalityStandardDTO updatedStandard = personalityStandardService.updatePersonalityStandard(id, personalityStandardDTO);
        return ResponseEntity.ok(updatedStandard);
    }
}

