
package com.reconx.backend.controller;

import com.reconx.backend.dto.RefundReconciliationResponse;
import com.reconx.backend.entity.RefundReconciliationRecord;
import com.reconx.backend.service.RefundReconciliationService;
import com.reconx.backend.service.RefundReconciliationRecordService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation/refunds")
public class RefundReconciliationController {

    private final RefundReconciliationService reconciliationService;
    private final RefundReconciliationRecordService recordService;

    public RefundReconciliationController(
            RefundReconciliationService reconciliationService,
            RefundReconciliationRecordService recordService) {

        this.reconciliationService = reconciliationService;
        this.recordService = recordService;
    }

    // Calculate without saving history
    @GetMapping
    public List<RefundReconciliationResponse> reconcileAll() {
        return reconciliationService.reconcileAll();
    }

    // Calculate and save history
    @PostMapping("/run")
    public List<RefundReconciliationResponse> runReconciliation() {
        return reconciliationService.runAndSave();
    }

    // Get all reconciliation history
    @GetMapping("/history")
    public List<RefundReconciliationRecord> getAllHistory() {
        return recordService.getAllHistory();
    }

    // Get history for a specific return
    @GetMapping("/history/{returnReference}")
    public List<RefundReconciliationRecord> getHistoryByReturnReference(
            @PathVariable String returnReference) {

        return recordService.getHistoryByReturnReference(returnReference);
    }
}
