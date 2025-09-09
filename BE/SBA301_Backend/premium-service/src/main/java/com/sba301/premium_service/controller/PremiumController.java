package com.sba301.premium_service.controller;

import com.sba301.premium_service.dto.PremiumCreateRequest;
import com.sba301.premium_service.dto.PremiumResponse;
import com.sba301.premium_service.dto.PremiumUpdateRequest;
import com.sba301.premium_service.service.PremiumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Premium", description = "Premium Management API")
@RequestMapping("/premiums")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping
    public ResponseEntity<Integer> createPremium(@RequestBody PremiumCreateRequest premiumCreateRequest) {
        return ResponseEntity.created(premiumService.createPremium(premiumCreateRequest)).build();
    }

    @GetMapping
    public ResponseEntity<List<PremiumResponse>> getPremiums() {
        return ResponseEntity.ok(premiumService.getPremiums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PremiumResponse> getPremium(@PathVariable int id) {
        return ResponseEntity.ok(premiumService.getPremium(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PremiumResponse> updatePremium(@PathVariable int id, @RequestBody PremiumUpdateRequest premiumUpdateRequest) {
        return ResponseEntity.ok(premiumService.updatePremium(id, premiumUpdateRequest));
    }
}
