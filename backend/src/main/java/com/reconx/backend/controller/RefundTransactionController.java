
package com.reconx.backend.controller;

import com.reconx.backend.entity.RefundTransaction;
import com.reconx.backend.service.RefundTransactionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
public class RefundTransactionController {

    private final RefundTransactionService refundService;

    public RefundTransactionController(
            RefundTransactionService refundService) {
        this.refundService = refundService;
    }

    // Create a refund transaction
    @PostMapping
    public RefundTransaction createRefund(
            @RequestBody RefundTransaction refund) {
        return refundService.createRefund(refund);
    }

    // Get all refund transactions
    @GetMapping
    public List<RefundTransaction> getAllRefunds() {
        return refundService.getAllRefunds();
    }

    // Get refund by reference
    @GetMapping("/{reference}")
    public RefundTransaction getRefundByReference(
            @PathVariable String reference) {
        return refundService.getRefundByReference(reference);
    }

    // Get refunds by return reference
    @GetMapping("/return/{returnReference}")
    public List<RefundTransaction> getRefundsByReturn(
            @PathVariable String returnReference) {
        return refundService.getRefundsByReturn(returnReference);
    }

    // Get refunds by order number
    @GetMapping("/order/{orderNumber}")
    public List<RefundTransaction> getRefundsByOrder(
            @PathVariable String orderNumber) {
        return refundService.getRefundsByOrder(orderNumber);
    }
}
