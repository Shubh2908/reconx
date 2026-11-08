
package com.reconx.backend.dto;

import com.reconx.backend.entity.RefundReconciliationStatus;

import java.math.BigDecimal;

public record RefundReconciliationResponse(

        String returnReference,

        String orderNumber,

        BigDecimal expectedRefundAmount,

        BigDecimal actualRefundAmount,

        BigDecimal refundDiscrepancy,

        RefundReconciliationStatus status

) {
}
