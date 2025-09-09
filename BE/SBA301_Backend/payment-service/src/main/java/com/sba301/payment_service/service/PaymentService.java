package com.sba301.payment_service.service;

import com.sba301.payment_service.dto.response.BillResponse;
import com.sba301.payment_service.entity.Payment;
import com.sba301.payment_service.entity.Transaction;

public interface PaymentService {
     Payment createPaymentByTransaction(Transaction transaction, String paymentId);

     BillResponse getSuccessPaymentByOrderId(Long orderId, String userId);
}
