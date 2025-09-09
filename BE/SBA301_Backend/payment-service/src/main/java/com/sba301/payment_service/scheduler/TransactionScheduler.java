package com.sba301.payment_service.scheduler;

import com.sba301.payment_service.client.OrderServiceClient;
import com.sba301.payment_service.client.PaymentServiceClient;
import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import com.sba301.payment_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionScheduler {
    @Value("${JOB_TIMEOUT}")
    private Long JOB_TIMEOUT;
    private final TransactionRepository transactionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderServiceClient orderServiceClient;


    @Scheduled(fixedRate = 60_000) // every 1 minute
    public void cancelOverdueTransactions() {
        LocalDateTime overdueTime = LocalDateTime.now().minusMinutes(5);

        List<Transaction> overdueTransactions = transactionRepository
                .findOverdueTransactions(overdueTime);

        for (Transaction transaction : overdueTransactions) {
            try {
                orderServiceClient.abandonOrder(Math.toIntExact(transaction.getOrderId()));
                paymentServiceClient.cancelTransaction(String.valueOf(transaction.getTransactionId()));
                log.info("Cancelled transaction: {}", transaction.getTransactionId());
            } catch (Exception e) {
                log.error("Failed to cancel transaction {}: {}", transaction.getTransactionId(), e.getMessage());
            }
        }
    }

}