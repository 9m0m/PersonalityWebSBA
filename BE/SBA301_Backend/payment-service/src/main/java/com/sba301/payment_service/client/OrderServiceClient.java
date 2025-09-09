package com.sba301.payment_service.client;

import com.sba301.payment_service.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/orders/{id}")
    ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") Integer id);

    @PostMapping("/orders/{orderId}/complete")
    ResponseEntity<OrderResponse> completeOrder(@PathVariable("orderId") Integer orderId);

    @PostMapping("/orders/{orderId}/abandon")
    public ResponseEntity<OrderResponse> abandonOrder(@PathVariable("orderId") Integer orderId);
}

