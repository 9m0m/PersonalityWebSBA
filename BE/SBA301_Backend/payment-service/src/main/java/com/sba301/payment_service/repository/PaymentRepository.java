package com.sba301.payment_service.repository;

import com.sba301.payment_service.entity.Payment;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    @Query("SELECT p FROM Payment p JOIN p.transaction t WHERE t.orderId = :orderId AND t.status = :status")
    Optional<Payment> findByOrderIdAndTransactionStatus(@Param("orderId") Long orderId, @Param("status") TransactionStatus status);
}
