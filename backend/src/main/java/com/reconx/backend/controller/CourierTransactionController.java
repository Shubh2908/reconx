
package com.reconx.backend.controller;

import com.reconx.backend.dto.CourierTransactionRequest;
import com.reconx.backend.entity.CourierTransaction;
import com.reconx.backend.service.CourierTransactionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courier-transactions")
public class CourierTransactionController {

    private final CourierTransactionService service;

    public CourierTransactionController(
            CourierTransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourierTransaction createTransaction(
            @Valid @RequestBody CourierTransactionRequest request) {

        return service.createTransaction(request);
    }

    @GetMapping
    public List<CourierTransaction> getAllTransactions() {
        return service.getAllTransactions();
    }
}
