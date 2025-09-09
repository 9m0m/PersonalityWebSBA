package com.sba301.premium_service.controller;

import com.sba301.premium_service.dto.SubcriptionRequest;
import com.sba301.premium_service.dto.SubcriptionResponse;
import com.sba301.premium_service.service.SubcriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SubcriptionController {

    private final SubcriptionService subcriptionService;

    public SubcriptionController(SubcriptionService subcriptionService) {
        this.subcriptionService = subcriptionService;
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubcriptionResponse>> getSubscriptions(
            @RequestParam(required = false) String uid,
            @RequestParam(required = false) Integer premiumId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ResponseEntity.ok(subcriptionService.getSubscriptions(
                uid,
                premiumId,
                status,
                from,
                to
        ));
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Integer> createSubscription(@RequestBody SubcriptionRequest subcriptionRequest) {
        return ResponseEntity.created(subcriptionService.createSubscription(subcriptionRequest)).build();
    }
}
