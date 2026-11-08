package com.reconx.backend.service;

import com.reconx.backend.dto.DashboardAnalyticsResponse;
import com.reconx.backend.entity.*;
import com.reconx.backend.repository.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardAnalyticsService {

    private final OrderRepository orderRepository;
    private final ReconciliationRecordRepository reconciliationRepository;
    private final CourierReconciliationRecordRepository courierHistoryRepository;
    private final CourierTransactionRepository courierTransactionRepository;
    private final ReturnRecordRepository returnRepository;

    public DashboardAnalyticsService(
            OrderRepository orderRepository,
            ReconciliationRecordRepository reconciliationRepository,
            CourierReconciliationRecordRepository courierHistoryRepository,
            CourierTransactionRepository courierTransactionRepository,
            ReturnRecordRepository returnRepository) {

        this.orderRepository = orderRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.courierHistoryRepository = courierHistoryRepository;
        this.courierTransactionRepository = courierTransactionRepository;
        this.returnRepository = returnRepository;
    }

    public DashboardAnalyticsResponse getDashboardAnalytics() {

        // PAYMENT ANALYTICS

        long totalOrders = orderRepository.count();

        List<ReconciliationRecord> records =
                reconciliationRepository.findAll();

        Map<String, ReconciliationRecord> latestRecords = new HashMap<>();

        for (ReconciliationRecord record : records) {

            ReconciliationRecord existing =
                    latestRecords.get(record.getOrderNumber());

            if (existing == null ||
                    (record.getReconciledAt() != null &&
                            (existing.getReconciledAt() == null ||
                                    record.getReconciledAt().isAfter(
                                            existing.getReconciledAt())))) {

                latestRecords.put(record.getOrderNumber(), record);
            }
        }

        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalReceived = BigDecimal.ZERO;
        BigDecimal totalDiscrepancy = BigDecimal.ZERO;

        long matched = 0;
        long mismatched = 0;
        long missing = 0;
        long failed = 0;

        for (ReconciliationRecord record : latestRecords.values()) {

            BigDecimal expected = record.getExpectedAmount() == null
                    ? BigDecimal.ZERO : record.getExpectedAmount();

            BigDecimal received = record.getReceivedAmount() == null
                    ? BigDecimal.ZERO : record.getReceivedAmount();

            BigDecimal discrepancy = record.getDiscrepancy() == null
                    ? BigDecimal.ZERO : record.getDiscrepancy();

            totalExpected = totalExpected.add(expected);
            totalReceived = totalReceived.add(received);
            totalDiscrepancy = totalDiscrepancy.add(discrepancy.abs());

            ReconciliationStatus status = record.getStatus();

            if (status == ReconciliationStatus.MATCHED) {
                matched++;
            } else if (status == ReconciliationStatus.AMOUNT_MISMATCH) {
                mismatched++;
            } else if (status == ReconciliationStatus.PAYMENT_MISSING) {
                missing++;
            } else if (status == ReconciliationStatus.PAYMENT_FAILED) {
                failed++;
            }
        }

        BigDecimal reconciliationRate = totalOrders == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(matched)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalOrders), 2,
                        RoundingMode.HALF_UP);

        // COURIER ANALYTICS

        List<CourierReconciliationRecord> courierRecords =
                courierHistoryRepository.findAll();

        Map<String, CourierReconciliationRecord> latestCourierRecords =
                new HashMap<>();

        for (CourierReconciliationRecord record : courierRecords) {

            CourierReconciliationRecord existing =
                    latestCourierRecords.get(record.getTrackingNumber());

            if (existing == null ||
                    (record.getReconciledAt() != null &&
                            (existing.getReconciledAt() == null ||
                                    record.getReconciledAt().isAfter(
                                            existing.getReconciledAt())))) {

                latestCourierRecords.put(record.getTrackingNumber(), record);
            }
        }

        long courierDiscrepancies = latestCourierRecords.values()
                .stream()
                .filter(record ->
                        record.getStatus() != CourierReconciliationStatus.MATCHED)
                .count();

        BigDecimal pendingCodSettlement = courierTransactionRepository
                .findAll()
                .stream()
                .filter(transaction ->
                        transaction.getSettlementStatus()
                                == CourierSettlementStatus.PENDING)
                .map(transaction ->
                        transaction.getCodCollected()
                                .subtract(transaction.getCodSettled()))
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // RETURNS AND REFUNDS ANALYTICS

        List<ReturnRecord> returnRecords = returnRepository.findAll();

        long totalReturns = returnRecords.size();

        BigDecimal totalExpectedRefunds = BigDecimal.ZERO;
        BigDecimal completedRefunds = BigDecimal.ZERO;
        BigDecimal pendingRefunds = BigDecimal.ZERO;

        for (ReturnRecord record : returnRecords) {

            BigDecimal amount = record.getExpectedRefundAmount() == null
                    ? BigDecimal.ZERO
                    : record.getExpectedRefundAmount();

            totalExpectedRefunds =
                    totalExpectedRefunds.add(amount);

            if (record.getReturnStatus() == ReturnStatus.REFUNDED) {

                completedRefunds = completedRefunds.add(amount);

            } else if (
                    record.getReturnStatus() == ReturnStatus.REFUND_PENDING ||
                            record.getReturnStatus() == ReturnStatus.RECEIVED) {

                pendingRefunds = pendingRefunds.add(amount);
            }
        }

        // FINAL DASHBOARD RESPONSE

        return new DashboardAnalyticsResponse(
                totalOrders,
                totalExpected,
                totalReceived,
                totalDiscrepancy,
                matched,
                mismatched,
                missing,
                failed,
                reconciliationRate,
                courierDiscrepancies,
                pendingCodSettlement,
                totalReturns,
                totalExpectedRefunds,
                completedRefunds,
                pendingRefunds
        );
    }
}