
package com.reconx.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courier_transactions")
public class CourierTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courierReference;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal actualWeight;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal actualShippingCharge;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal codCollected;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal codSettled;

    @Enumerated(EnumType.STRING)
    private CourierSettlementStatus settlementStatus;

    private LocalDateTime transactionDate;

    public CourierTransaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourierReference() {
        return courierReference;
    }

    public void setCourierReference(String courierReference) {
        this.courierReference = courierReference;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public BigDecimal getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(BigDecimal actualWeight) {
        this.actualWeight = actualWeight;
    }

    public BigDecimal getActualShippingCharge() {
        return actualShippingCharge;
    }

    public void setActualShippingCharge(BigDecimal actualShippingCharge) {
        this.actualShippingCharge = actualShippingCharge;
    }

    public BigDecimal getCodCollected() {
        return codCollected;
    }

    public void setCodCollected(BigDecimal codCollected) {
        this.codCollected = codCollected;
    }

    public BigDecimal getCodSettled() {
        return codSettled;
    }

    public void setCodSettled(BigDecimal codSettled) {
        this.codSettled = codSettled;
    }

    public CourierSettlementStatus getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(CourierSettlementStatus settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
