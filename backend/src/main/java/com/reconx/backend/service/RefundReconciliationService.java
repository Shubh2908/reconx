
package com.reconx.backend.service;

import com.reconx.backend.dto.RefundReconciliationResponse;
import com.reconx.backend.entity.*;
import com.reconx.backend.repository.ReturnRecordRepository;
import com.reconx.backend.repository.RefundTransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RefundReconciliationService {

    private final ReturnRecordRepository returnRepository;
    private final RefundTransactionRepository refundRepository;
    private final RefundReconciliationRecordService recordService;

    public RefundReconciliationService(
            ReturnRecordRepository returnRepository,
            RefundTransactionRepository refundRepository,
            RefundReconciliationRecordService recordService) {

        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
        this.recordService = recordService;
    }

    // Calculate refund reconciliation without saving history
    public List<RefundReconciliationResponse> reconcileAll() {

        List<ReturnRecord> returns = returnRepository.findAll();

        List<RefundReconciliationResponse> results =
                new ArrayList<>();

        for (ReturnRecord returnRecord : returns) {

            List<RefundTransaction> refunds =
                    refundRepository.findAllByReturnReference(
                            returnRecord.getReturnReference());

            BigDecimal expectedAmount =
                    returnRecord.getExpectedRefundAmount();

            BigDecimal actualAmount = BigDecimal.ZERO;

            BigDecimal refundDiscrepancy;

            RefundReconciliationStatus status;

            if (refunds.isEmpty()) {

                status = RefundReconciliationStatus.REFUND_MISSING;
                refundDiscrepancy = expectedAmount;

            } else {

                BigDecimal successfulAmount = BigDecimal.ZERO;
                boolean hasPending = false;
                boolean hasFailed = false;

                for (RefundTransaction refund : refunds) {

                    if (refund.getRefundStatus() == RefundStatus.SUCCESS) {

                        successfulAmount = successfulAmount.add(
                                refund.getRefundAmount());

                    } else if (
                            refund.getRefundStatus() == RefundStatus.INITIATED ||
                                    refund.getRefundStatus() == RefundStatus.PROCESSING) {

                        hasPending = true;

                    } else if (
                            refund.getRefundStatus() == RefundStatus.FAILED) {

                        hasFailed = true;
                    }
                }

                actualAmount = successfulAmount;

                refundDiscrepancy =
                        expectedAmount.subtract(actualAmount);

                if (refunds.size() > 1) {

                    status = RefundReconciliationStatus.MULTIPLE_REFUNDS;

                } else if (hasPending) {

                    status = RefundReconciliationStatus.REFUND_PENDING;

                } else if (hasFailed && actualAmount.compareTo(
                        BigDecimal.ZERO) == 0) {

                    status = RefundReconciliationStatus.REFUND_FAILED;

                } else if (actualAmount.compareTo(
                        expectedAmount) == 0) {

                    status = RefundReconciliationStatus.REFUND_MATCHED;

                } else {

                    status = RefundReconciliationStatus.REFUND_AMOUNT_MISMATCH;
                }
            }

            RefundReconciliationResponse response =
                    new RefundReconciliationResponse(
                            returnRecord.getReturnReference(),
                            returnRecord.getOrderNumber(),
                            expectedAmount,
                            actualAmount,
                            refundDiscrepancy,
                            status
                    );

            results.add(response);
        }

        return results;
    }

    // Calculate reconciliation and save history
    public List<RefundReconciliationResponse> runAndSave() {

        List<RefundReconciliationResponse> results = reconcileAll();

        for (RefundReconciliationResponse response : results) {
            saveHistory(response);
        }

        return results;
    }

    // Save individual reconciliation result
    private void saveHistory(RefundReconciliationResponse response) {

        RefundReconciliationRecord record =
                new RefundReconciliationRecord();

        record.setReturnReference(response.returnReference());
        record.setOrderNumber(response.orderNumber());
        record.setExpectedRefundAmount(
                response.expectedRefundAmount());
        record.setActualRefundAmount(
                response.actualRefundAmount());
        record.setRefundDiscrepancy(
                response.refundDiscrepancy());
        record.setStatus(response.status());

        recordService.saveRecord(record);
    }
}
