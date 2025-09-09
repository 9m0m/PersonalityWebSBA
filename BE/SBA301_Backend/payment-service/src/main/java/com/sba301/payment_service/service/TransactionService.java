package com.sba301.payment_service.service;

import com.sba301.payment_service.entity.Payment;
import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import org.apache.coyote.BadRequestException;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransactionStatusByTransId(Long transId, TransactionStatus transactionStatus);

    Transaction cancelTransaction(Long transactionId, String cancellationReason);


}
