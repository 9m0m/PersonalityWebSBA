package com.sba301.payment_service.client;

import com.sba301.payment_service.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @GetMapping("/checkout/create-payment-link/{id}")
    ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") Integer id);

    @GetMapping("/checkout/cancel")
    ResponseEntity<Void> cancelTransaction(@RequestParam("orderCode") String transId);
}

