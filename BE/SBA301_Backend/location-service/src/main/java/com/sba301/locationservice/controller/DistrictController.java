package com.sba301.locationservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba301.locationservice.dto.District;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class DistrictController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/districts/{code}")
    public ResponseEntity<District> getDistrict(@PathVariable String code) {
        District district = restTemplate.getForObject(
                "https://provinces.open-api.vn/api/d/" + code, District.class
        );

        return ResponseEntity.ok(district);
    }
}
