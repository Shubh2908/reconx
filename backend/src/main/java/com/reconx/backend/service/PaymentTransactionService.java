
package com.reconx.backend.service;

import com.reconx.backend.dto.PaymentTransactionRequest;
import com.reconx.backend.entity.PaymentTransaction;
import com.reconx.backend.repository.PaymentTransactionRepository;

import org.springframework.stereotype.Service;

@Service
public class PaymentTransactionService {

    private final PaymentTransactionRepository repository;

    public PaymentTransactionService(
            PaymentTransactionRepository repository) {
        this.repository = repository;
    }

    public PaymentTransaction createTransaction(
            PaymentTransactionRequest request) {

        if (repository.existsByTransactionReference(
                request.getTransactionReference())) {

            throw new IllegalArgumentException(
                    "Transaction reference already exists");
        }

        PaymentTransaction transaction = new PaymentTransaction();

        transaction.setTransactionReference(
                request.getTransactionReference());

        transaction.setOrderNumber(request.getOrderNumber());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(request.getStatus());
        transaction.setTransactionDate(
                request.getTransactionDate());

        return repository.save(transaction);
    }
}
