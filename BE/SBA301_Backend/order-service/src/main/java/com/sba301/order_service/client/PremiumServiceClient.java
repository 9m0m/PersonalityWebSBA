package com.sba301.order_service.client;

import com.sba301.order_service.dto.request.SubcriptionRequest;
import com.sba301.order_service.dto.response.PremiumResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "premium-service")
public interface PremiumServiceClient {
    @GetMapping("/premiums/{id}")
    ResponseEntity<PremiumResponse> getPremiumById(@PathVariable("id") Integer id);


    // subscriptions
    @PostMapping("/subscriptions")
    public ResponseEntity<Integer> createSubscription(@RequestBody SubcriptionRequest subcriptionRequest);
}

