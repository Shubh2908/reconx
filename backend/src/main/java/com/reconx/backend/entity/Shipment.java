
package com.reconx.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String courierName;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal expectedWeight;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal expectedShippingCharge;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal expectedCodAmount;

    private LocalDateTime shipmentDate;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    public Shipment() {
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public BigDecimal getExpectedWeight() {
        return expectedWeight;
    }

    public void setExpectedWeight(BigDecimal expectedWeight) {
        this.expectedWeight = expectedWeight;
    }

    public BigDecimal getExpectedShippingCharge() {
        return expectedShippingCharge;
    }

    public void setExpectedShippingCharge(BigDecimal expectedShippingCharge) {
        this.expectedShippingCharge = expectedShippingCharge;
    }

    public BigDecimal getExpectedCodAmount() {
        return expectedCodAmount;
    }

    public void setExpectedCodAmount(BigDecimal expectedCodAmount) {
        this.expectedCodAmount = expectedCodAmount;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }
}
