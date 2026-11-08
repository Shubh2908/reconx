
package com.reconx.backend.controller;

import com.reconx.backend.dto.PaymentReconciliationResponse;
import com.reconx.backend.entity.ReconciliationRecord;
import com.reconx.backend.service.PaymentReconciliationService;
import com.reconx.backend.service.ReconciliationRecordService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation")
public class PaymentReconciliationController {

    private final PaymentReconciliationService reconciliationService;
    private final ReconciliationRecordService recordService;

    public PaymentReconciliationController(
            PaymentReconciliationService reconciliationService,
            ReconciliationRecordService recordService) {

        this.reconciliationService = reconciliationService;
        this.recordService = recordService;
    }

    // Calculate results without saving history
    @GetMapping("/payments")
    public List<PaymentReconciliationResponse> reconcilePayments() {
        return reconciliationService.reconcilePayments();
    }

    // Execute reconciliation and save history
    @PostMapping("/payments/run")
    public List<PaymentReconciliationResponse> runReconciliation() {
        return reconciliationService.reconcileAndSavePayments();
    }

    // Get complete reconciliation history
    @GetMapping("/history")
    public List<ReconciliationRecord> getAllHistory() {
        return recordService.getAllHistory();
    }

    // Get reconciliation history for one order
    @GetMapping("/history/{orderNumber}")
    public List<ReconciliationRecord> getHistoryByOrder(
            @PathVariable String orderNumber) {

        return recordService.getHistoryByOrder(orderNumber);
    }
}
