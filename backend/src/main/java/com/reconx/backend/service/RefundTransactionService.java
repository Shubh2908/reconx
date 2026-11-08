
package com.reconx.backend.service;

import com.reconx.backend.entity.RefundTransaction;
import com.reconx.backend.entity.RefundStatus;
import com.reconx.backend.entity.ReturnRecord;
import com.reconx.backend.repository.RefundTransactionRepository;
import com.reconx.backend.repository.ReturnRecordRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefundTransactionService {

    private final RefundTransactionRepository refundRepository;
    private final ReturnRecordRepository returnRepository;

    public RefundTransactionService(
            RefundTransactionRepository refundRepository,
            ReturnRecordRepository returnRepository) {

        this.refundRepository = refundRepository;
        this.returnRepository = returnRepository;
    }

    public RefundTransaction createRefund(RefundTransaction refund) {

        ReturnRecord returnRecord =
                returnRepository.findByReturnReference(
                        refund.getReturnReference()
                ).orElseThrow(() ->
                        new RuntimeException("Return record not found")
                );

        if (refundRepository.existsByRefundReference(
                refund.getRefundReference())) {
            throw new RuntimeException(
                    "Refund reference already exists");
        }

        if (refund.getRefundAmount() == null ||
                refund.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Refund amount must be greater than zero");
        }

        if (!returnRecord.getOrderNumber().equals(
                refund.getOrderNumber())) {
            throw new RuntimeException(
                    "Order number does not match the return record");
        }

        if (refund.getRefundAmount().compareTo(
                returnRecord.getExpectedRefundAmount()) > 0) {
            throw new RuntimeException(
                    "Refund amount cannot exceed expected refund amount");
        }

        if (refund.getRefundStatus() == null) {
            refund.setRefundStatus(RefundStatus.INITIATED);
        }

        if (refund.getRefundDate() == null) {
            refund.setRefundDate(LocalDateTime.now());
        }

        return refundRepository.save(refund);
    }

    public List<RefundTransaction> getAllRefunds() {
        return refundRepository.findAll();
    }

    public RefundTransaction getRefundByReference(String reference) {
        return refundRepository.findByRefundReference(reference)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Refund not found: " + reference));
    }

    public List<RefundTransaction> getRefundsByReturn(
            String returnReference) {

        return refundRepository.findAllByReturnReference(
                returnReference);
    }

    public List<RefundTransaction> getRefundsByOrder(
            String orderNumber) {

        return refundRepository.findAllByOrderNumber(orderNumber);
    }
}
