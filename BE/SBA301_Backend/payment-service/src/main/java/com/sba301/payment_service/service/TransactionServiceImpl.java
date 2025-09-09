package com.sba301.payment_service.service;

import com.sba301.payment_service.entity.Payment;
import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import com.sba301.payment_service.exception.AppException;
import com.sba301.payment_service.repository.TransactionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.PaymentLinkData;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;
    PayOS payOS;

    public TransactionServiceImpl(TransactionRepository transactionRepository, PayOS payOS) {
        this.transactionRepository = transactionRepository;
        this.payOS = payOS;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionStatusByTransId(Long transId, TransactionStatus transactionStatus) {
        Transaction trans = transactionRepository.findFirstByTransactionIdAndStatusOrderByTimeDesc(transId, TransactionStatus.PENDING);
        if (trans == null) throw new IllegalArgumentException("Order not found: " + transId);
        trans.setStatus(transactionStatus);
        return transactionRepository.save(trans);
    }

    @Override
    public Transaction cancelTransaction(Long transactionId, String cancellationReason) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new AppException(404, "Transaction Not Found", HttpStatus.NOT_FOUND));
        try {
            PaymentLinkData data = payOS.cancelPaymentLink(transaction.getOrderId(), cancellationReason);
            transaction.setStatus(TransactionStatus.CANCEL);
            transaction.setDescription(cancellationReason);
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
