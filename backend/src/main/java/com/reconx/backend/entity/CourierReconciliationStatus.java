
package com.reconx.backend.entity;

public enum CourierReconciliationStatus {

    MATCHED,

    WEIGHT_MISMATCH,

    SHIPPING_CHARGE_MISMATCH,

    COD_COLLECTION_MISMATCH,

    COD_SETTLEMENT_MISMATCH,

    MULTIPLE_DISCREPANCIES,

    COURIER_TRANSACTION_MISSING
}
