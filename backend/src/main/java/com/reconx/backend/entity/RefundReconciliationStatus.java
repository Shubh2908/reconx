package com.reconx.backend.entity;

public enum RefundReconciliationStatus {
    REFUND_MATCHED,
    REFUND_MISSING,
    REFUND_AMOUNT_MISMATCH,
    REFUND_PENDING,
    REFUND_FAILED,
    MULTIPLE_REFUNDS
}