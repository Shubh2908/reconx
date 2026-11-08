
package com.reconx.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courier_reconciliation_records")
public class CourierReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trackingNumber;

    private BigDecimal weightDiscrepancy;

    private BigDecimal shippingChargeDiscrepancy;

    private BigDecimal codCollectionDiscrepancy;

    private BigDecimal codSettlementDiscrepancy;

    @Enumerated(EnumType.STRING)
    private CourierReconciliationStatus status;

    private LocalDateTime reconciledAt;

    public CourierReconciliationRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public BigDecimal getWeightDiscrepancy() {
        return weightDiscrepancy;
    }

    public void setWeightDiscrepancy(BigDecimal weightDiscrepancy) {
        this.weightDiscrepancy = weightDiscrepancy;
    }

    public BigDecimal getShippingChargeDiscrepancy() {
        return shippingChargeDiscrepancy;
    }

    public void setShippingChargeDiscrepancy(BigDecimal shippingChargeDiscrepancy) {
        this.shippingChargeDiscrepancy = shippingChargeDiscrepancy;
    }

    public BigDecimal getCodCollectionDiscrepancy() {
        return codCollectionDiscrepancy;
    }

    public void setCodCollectionDiscrepancy(BigDecimal codCollectionDiscrepancy) {
        this.codCollectionDiscrepancy = codCollectionDiscrepancy;
    }

    public CourierReconciliationStatus getStatus() {
        return status;
    }

    public void setStatus(CourierReconciliationStatus status) {
        this.status = status;
    }

    public BigDecimal getCodSettlementDiscrepancy() {
        return codSettlementDiscrepancy;
    }

    public void setCodSettlementDiscrepancy(BigDecimal codSettlementDiscrepancy) {
        this.codSettlementDiscrepancy = codSettlementDiscrepancy;
    }

    public LocalDateTime getReconciledAt() {
        return reconciledAt;
    }

    public void setReconciledAt(LocalDateTime reconciledAt) {
        this.reconciledAt = reconciledAt;
    }
}
