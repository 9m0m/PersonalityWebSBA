package com.sba301.payment_service.controller;

import com.sba301.payment_service.dto.response.BillResponse;
import com.sba301.payment_service.service.PaymentService;
import com.sba301.payment_service.util.HeaderExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<BillResponse> getSuccessTransaction(@PathVariable Long orderId, HttpServletRequest request) {
        String userId = HeaderExtractor.getUserId(request);
        BillResponse billResponse = paymentService.getSuccessPaymentByOrderId(orderId, userId);
        return ResponseEntity.ok(billResponse);
    }

}