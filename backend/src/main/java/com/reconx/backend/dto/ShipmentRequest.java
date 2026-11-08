
package com.reconx.backend.dto;

import com.reconx.backend.entity.ShipmentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShipmentRequest {

    @NotBlank
    private String trackingNumber;

    @NotBlank
    private String orderNumber;

    @NotBlank
    private String courierName;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal expectedWeight;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal expectedShippingCharge;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal expectedCodAmount;

    @NotNull
    private LocalDateTime shipmentDate;

    @NotNull
    private ShipmentStatus shipmentStatus;

    public @NotBlank String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(@NotBlank String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public @NotBlank String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(@NotBlank String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public @NotBlank String getCourierName() {
        return courierName;
    }

    public void setCourierName(@NotBlank String courierName) {
        this.courierName = courierName;
    }

    public @NotNull @DecimalMin("0.00") BigDecimal getExpectedShippingCharge() {
        return expectedShippingCharge;
    }

    public void setExpectedShippingCharge(@NotNull @DecimalMin("0.00") BigDecimal expectedShippingCharge) {
        this.expectedShippingCharge = expectedShippingCharge;
    }

    public @NotNull @DecimalMin("0.001") BigDecimal getExpectedWeight() {
        return expectedWeight;
    }

    public void setExpectedWeight(@NotNull @DecimalMin("0.001") BigDecimal expectedWeight) {
        this.expectedWeight = expectedWeight;
    }

    public @NotNull @DecimalMin("0.00") BigDecimal getExpectedCodAmount() {
        return expectedCodAmount;
    }

    public void setExpectedCodAmount(@NotNull @DecimalMin("0.00") BigDecimal expectedCodAmount) {
        this.expectedCodAmount = expectedCodAmount;
    }

    public @NotNull LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(@NotNull LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public @NotNull ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(@NotNull ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }
}
