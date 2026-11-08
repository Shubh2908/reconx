
package com.reconx.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_records")
public class ReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String returnReference;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal expectedRefundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus returnStatus;

    private String returnReason;

    private LocalDateTime returnDate;

    public ReturnRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getReturnReference() {
        return returnReference;
    }

    public void setReturnReference(String returnReference) {
        this.returnReference = returnReference;
    }

    public BigDecimal getExpectedRefundAmount() {
        return expectedRefundAmount;
    }

    public void setExpectedRefundAmount(BigDecimal expectedRefundAmount) {
        this.expectedRefundAmount = expectedRefundAmount;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
}
