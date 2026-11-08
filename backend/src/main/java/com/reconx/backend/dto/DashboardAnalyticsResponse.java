package com.reconx.backend.dto;

import java.math.BigDecimal;

public record DashboardAnalyticsResponse(
        long totalOrders,
        BigDecimal totalExpectedAmount,
        BigDecimal totalReceivedAmount,
        BigDecimal totalDiscrepancy,
        long matchedPayments,
        long amountMismatches,
        long missingPayments,
        long failedPayments,
        BigDecimal reconciliationRate,

        // Courier analytics
        long courierDiscrepancies,
        BigDecimal pendingCodSettlement,

        // Returns and refunds
        long totalReturns,
        BigDecimal totalExpectedRefunds,
        BigDecimal completedRefunds,
        BigDecimal pendingRefunds
) {
}