
package com.reconx.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_reconciliation_records")
public class RefundReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String returnReference;

    private String orderNumber;

    private BigDecimal expectedRefundAmount;

    private BigDecimal actualRefundAmount;

    private BigDecimal refundDiscrepancy;

    @Enumerated(EnumType.STRING)
    private RefundReconciliationStatus status;

    private LocalDateTime reconciledAt;

    public RefundReconciliationRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReturnReference() {
        return returnReference;
    }

    public void setReturnReference(String returnReference) {
        this.returnReference = returnReference;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getExpectedRefundAmount() {
        return expectedRefundAmount;
    }

    public void setExpectedRefundAmount(BigDecimal expectedRefundAmount) {
        this.expectedRefundAmount = expectedRefundAmount;
    }

    public BigDecimal getActualRefundAmount() {
        return actualRefundAmount;
    }

    public void setActualRefundAmount(BigDecimal actualRefundAmount) {
        this.actualRefundAmount = actualRefundAmount;
    }

    public BigDecimal getRefundDiscrepancy() {
        return refundDiscrepancy;
    }

    public void setRefundDiscrepancy(BigDecimal refundDiscrepancy) {
        this.refundDiscrepancy = refundDiscrepancy;
    }

    public RefundReconciliationStatus getStatus() {
        return status;
    }

    public void setStatus(RefundReconciliationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReconciledAt() {
        return reconciledAt;
    }

    public void setReconciledAt(LocalDateTime reconciledAt) {
        this.reconciledAt = reconciledAt;
    }
}
