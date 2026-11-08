
package com.reconx.backend.service;

import com.reconx.backend.dto.PaymentReconciliationResponse;
import com.reconx.backend.entity.*;
import com.reconx.backend.repository.OrderRepository;
import com.reconx.backend.repository.PaymentTransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentReconciliationService {

    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final ReconciliationRecordService recordService;

    public PaymentReconciliationService(
            OrderRepository orderRepository,
            PaymentTransactionRepository transactionRepository,
            ReconciliationRecordService recordService) {

        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.recordService = recordService;
    }

    public List<PaymentReconciliationResponse> reconcilePayments() {

        List<Order> orders = orderRepository.findAll();

        List<PaymentReconciliationResponse> results =
                new ArrayList<>();

        for (Order order : orders) {

            List<PaymentTransaction> transactions =
                    transactionRepository.findAllByOrderNumber(
                            order.getOrderNumber());

            BigDecimal receivedAmount = BigDecimal.ZERO;
            boolean hasFailedTransaction = false;

            for (PaymentTransaction transaction : transactions) {

                if (transaction.getStatus()
                        == GatewayPaymentStatus.SUCCESS) {

                    receivedAmount = receivedAmount.add(
                            transaction.getAmount());

                } else if (transaction.getStatus()
                        == GatewayPaymentStatus.FAILED) {

                    hasFailedTransaction = true;
                }
            }

            ReconciliationStatus status;

            if (transactions.isEmpty()) {
                status = ReconciliationStatus.PAYMENT_MISSING;

            } else if (receivedAmount.compareTo(BigDecimal.ZERO) == 0
                    && hasFailedTransaction) {
                status = ReconciliationStatus.PAYMENT_FAILED;

            } else if (receivedAmount.compareTo(
                    order.getTotalAmount()) == 0) {
                status = ReconciliationStatus.MATCHED;

            } else {
                status = ReconciliationStatus.AMOUNT_MISMATCH;
            }

            BigDecimal discrepancy =
                    order.getTotalAmount().subtract(receivedAmount);

            PaymentReconciliationResponse response =
                    new PaymentReconciliationResponse(
                            order.getOrderNumber(),
                            order.getTotalAmount(),
                            receivedAmount,
                            discrepancy,
                            status
                    );

            results.add(response);
        }

        return results;
    }

    public List<PaymentReconciliationResponse>
    reconcileAndSavePayments() {

        List<PaymentReconciliationResponse> results =
                reconcilePayments();

        for (PaymentReconciliationResponse response : results) {

            ReconciliationRecord record =
                    new ReconciliationRecord();

            record.setOrderNumber(response.orderNumber());
            record.setExpectedAmount(response.expectedAmount());
            record.setReceivedAmount(response.receivedAmount());
            record.setDiscrepancy(response.discrepancy());
            record.setStatus(response.status());

            recordService.saveRecord(record);
        }

        return results;
    }
}
