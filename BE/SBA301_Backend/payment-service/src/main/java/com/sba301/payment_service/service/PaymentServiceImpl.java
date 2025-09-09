package com.sba301.payment_service.service;

import com.sba301.payment_service.dto.response.BillResponse;
import com.sba301.payment_service.entity.Payment;
import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import com.sba301.payment_service.exception.AppException;
import com.sba301.payment_service.repository.PaymentRepository;
import com.sba301.payment_service.service.PaymentService;
import com.sba301.payment_service.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PayOS payOS;
    private final TransactionService transactionService;

    @Override
    public Payment createPaymentByTransaction(Transaction transaction, String paymentId) {
        Payment payment = Payment.builder()
                .id(paymentId)
                .amount(transaction.getAmount())
                .fee(0L)
                .transaction(transaction)
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    public BillResponse getSuccessPaymentByOrderId(Long orderId, String userId) {
        // Find payment with successful transaction for the given orderId
        Payment payment = paymentRepository.findByOrderIdAndTransactionStatus(orderId, TransactionStatus.PAID)
                .orElseThrow(() -> new AppException(404, "No successful payment found for order: " + orderId, HttpStatus.NOT_FOUND));

        // Verify that the payment belongs to the requesting user
        if (!userId.equals(payment.getTransaction().getUid())) {
            throw new AppException(403, "You are not authorized to view this payment", HttpStatus.FORBIDDEN);
        }

        // Map to BillResponse and include transaction status
        BillResponse response = BillResponse.fromTransaction(payment);
        response.setStatus(payment.getTransaction().getStatus().name());

        return response;
    }
}
