
package com.reconx.backend.dto;

import com.reconx.backend.entity.CourierSettlementStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourierTransactionRequest {

    @NotBlank
    private String courierReference;

    @NotBlank
    private String trackingNumber;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal actualWeight;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal actualShippingCharge;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal codCollected;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal codSettled;

    @NotNull
    private CourierSettlementStatus settlementStatus;

    @NotNull
    private LocalDateTime transactionDate;

    public @NotBlank String getCourierReference() {
        return courierReference;
    }

    public void setCourierReference(@NotBlank String courierReference) {
        this.courierReference = courierReference;
    }

    public @NotBlank String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(@NotBlank String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public @NotNull @DecimalMin("0.001") BigDecimal getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(@NotNull @DecimalMin("0.001") BigDecimal actualWeight) {
        this.actualWeight = actualWeight;
    }

    public @NotNull @DecimalMin("0.00") BigDecimal getActualShippingCharge() {
        return actualShippingCharge;
    }

    public void setActualShippingCharge(@NotNull @DecimalMin("0.00") BigDecimal actualShippingCharge) {
        this.actualShippingCharge = actualShippingCharge;
    }

    public @NotNull @DecimalMin("0.00") BigDecimal getCodCollected() {
        return codCollected;
    }

    public void setCodCollected(@NotNull @DecimalMin("0.00") BigDecimal codCollected) {
        this.codCollected = codCollected;
    }

    public @NotNull @DecimalMin("0.00") BigDecimal getCodSettled() {
        return codSettled;
    }

    public void setCodSettled(@NotNull @DecimalMin("0.00") BigDecimal codSettled) {
        this.codSettled = codSettled;
    }

    public @NotNull CourierSettlementStatus getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(@NotNull CourierSettlementStatus settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public @NotNull LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(@NotNull LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
