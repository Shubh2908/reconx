
package com.reconx.backend.controller;

import com.reconx.backend.dto.CourierReconciliationResponse;
import com.reconx.backend.service.CourierReconciliationService;
import com.reconx.backend.service.CourierReconciliationRecordService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation/courier")
public class CourierReconciliationController {

    private final CourierReconciliationService reconciliationService;
    private final CourierReconciliationRecordService recordService;

    public CourierReconciliationController(
            CourierReconciliationService reconciliationService,
            CourierReconciliationRecordService recordService) {

        this.reconciliationService = reconciliationService;
        this.recordService = recordService;
    }

    // Calculate discrepancies without saving history
    @GetMapping
    public List<CourierReconciliationResponse> reconcileAll() {
        return reconciliationService.reconcileAll();
    }

    // Calculate discrepancies and save history
    @PostMapping("/run")
    public List<CourierReconciliationResponse> runReconciliation() {
        return reconciliationService.runAndSave();
    }

    // View complete reconciliation history
    @GetMapping("/history")
    public Object getAllHistory() {
        return recordService.getAllHistory();
    }

    // View history for a specific tracking number
    @GetMapping("/history/{trackingNumber}")
    public Object getHistoryByTrackingNumber(
            @PathVariable String trackingNumber) {

        return recordService.getHistoryByTrackingNumber(trackingNumber);
    }
}
