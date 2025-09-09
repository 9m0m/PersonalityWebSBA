package com.sba301.payment_service.repository;

import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long transactionId);

    Transaction findFirstByTransactionIdAndStatusOrderByTimeDesc(Long transactionId, TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' AND t.time <= :timeLimit")
    List<Transaction> findOverdueTransactions(
            @Param("timeLimit") LocalDateTime timeLimit);
}
