package com.reconx.backend.dto;

import com.reconx.backend.entity.ReconciliationStatus;
import java.math.BigDecimal;

public record PaymentReconciliationResponse(
        String orderNumber,
        BigDecimal expectedAmount,
        BigDecimal receivedAmount,
        BigDecimal discrepancy,
        ReconciliationStatus status
) {
}