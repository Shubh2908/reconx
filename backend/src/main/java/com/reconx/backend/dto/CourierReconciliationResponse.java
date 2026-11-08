
package com.reconx.backend.dto;

import com.reconx.backend.entity.CourierReconciliationStatus;
import java.math.BigDecimal;

public record CourierReconciliationResponse(

        String trackingNumber,

        BigDecimal expectedWeight,
        BigDecimal actualWeight,
        BigDecimal weightDiscrepancy,

        BigDecimal expectedShippingCharge,
        BigDecimal actualShippingCharge,
        BigDecimal shippingChargeDiscrepancy,

        BigDecimal expectedCodAmount,
        BigDecimal codCollected,
        BigDecimal codCollectionDiscrepancy,

        BigDecimal codSettled,
        BigDecimal codSettlementDiscrepancy,

        CourierReconciliationStatus status

) {
}
