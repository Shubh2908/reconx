
package com.reconx.backend.dto;

import com.reconx.backend.entity.GatewayPaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentTransactionRequest {

    @NotBlank
    private String transactionReference;

    @NotBlank
    private String orderNumber;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private GatewayPaymentStatus status;

    @NotNull
    private LocalDateTime transactionDate;

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public GatewayPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(GatewayPaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
