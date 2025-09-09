package com.sba301.locationservice.controller;

import com.sba301.locationservice.dto.District;
import com.sba301.locationservice.dto.Province;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProvinceController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/provinces/{code}")
    public ResponseEntity<Province> getProvinces(@PathVariable(required = false) String code) {

        Province provinces = restTemplate.getForObject(
                "https://provinces.open-api.vn/api/p/" + code + "?depth=2", Province.class
        );

        return ResponseEntity.ok(provinces);
    }


}
