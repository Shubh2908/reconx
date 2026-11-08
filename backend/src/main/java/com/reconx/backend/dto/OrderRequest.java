
package com.reconx.backend.dto;

import com.reconx.backend.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderRequest {

    @NotBlank
    private String orderNumber;

    public @NotNull OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(@NotNull OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public @Email String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(@Email String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public @NotBlank String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(@NotBlank String customerName) {
        this.customerName = customerName;
    }

    public @NotNull LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(@NotNull LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public @NotNull @DecimalMin("0.01") BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(@NotNull @DecimalMin("0.01") BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public @NotNull PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(@NotNull PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public @NotNull PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@NotNull PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public @NotBlank String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(@NotBlank String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @NotBlank
    private String customerName;

    @Email
    private String customerEmail;

    @NotNull
    private LocalDateTime orderDate;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalAmount;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    private OrderStatus orderStatus;

    // Generate getters and setters using Alt + Insert
}
