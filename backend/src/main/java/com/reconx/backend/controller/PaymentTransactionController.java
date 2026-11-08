
package com.reconx.backend.controller;

import com.reconx.backend.dto.PaymentTransactionRequest;
import com.reconx.backend.entity.PaymentTransaction;
import com.reconx.backend.service.PaymentTransactionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-transactions")
public class PaymentTransactionController {

    private final PaymentTransactionService service;

    public PaymentTransactionController(
            PaymentTransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentTransaction createTransaction(
            @Valid @RequestBody PaymentTransactionRequest request) {

        return service.createTransaction(request);
    }
}
