
package com.reconx.backend.service;

import com.reconx.backend.dto.CourierReconciliationResponse;
import com.reconx.backend.entity.*;
import com.reconx.backend.repository.ShipmentRepository;
import com.reconx.backend.repository.CourierTransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourierReconciliationService {

    private final ShipmentRepository shipmentRepository;
    private final CourierTransactionRepository transactionRepository;
    private final CourierReconciliationRecordService recordService;

    public CourierReconciliationService(
            ShipmentRepository shipmentRepository,
            CourierTransactionRepository transactionRepository,
            CourierReconciliationRecordService recordService) {

        this.shipmentRepository = shipmentRepository;
        this.transactionRepository = transactionRepository;
        this.recordService = recordService;
    }

    // Calculate reconciliation without saving history
    public List<CourierReconciliationResponse> reconcileAll() {

        List<Shipment> shipments = shipmentRepository.findAll();

        List<CourierReconciliationResponse> results =
                new ArrayList<>();

        for (Shipment shipment : shipments) {

            List<CourierTransaction> transactions =
                    transactionRepository.findAllByTrackingNumber(
                            shipment.getTrackingNumber());

            if (transactions.isEmpty()) {

                CourierReconciliationResponse response =
                        new CourierReconciliationResponse(
                                shipment.getTrackingNumber(),

                                shipment.getExpectedWeight(),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,

                                shipment.getExpectedShippingCharge(),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,

                                shipment.getExpectedCodAmount(),
                                BigDecimal.ZERO,
                                shipment.getExpectedCodAmount(),

                                BigDecimal.ZERO,
                                BigDecimal.ZERO,

                                CourierReconciliationStatus.COURIER_TRANSACTION_MISSING
                        );

                results.add(response);
                continue;
            }

            // Initial version assumes one courier transaction per shipment.
            CourierTransaction transaction = transactions.get(0);

            BigDecimal weightDifference =
                    transaction.getActualWeight().subtract(
                            shipment.getExpectedWeight());

            BigDecimal shippingDifference =
                    transaction.getActualShippingCharge().subtract(
                            shipment.getExpectedShippingCharge());

            BigDecimal codCollectionDifference =
                    shipment.getExpectedCodAmount().subtract(
                            transaction.getCodCollected());

            BigDecimal codSettlementDifference =
                    transaction.getCodCollected().subtract(
                            transaction.getCodSettled());

            boolean weightMismatch =
                    weightDifference.compareTo(BigDecimal.ZERO) != 0;

            boolean shippingMismatch =
                    shippingDifference.compareTo(BigDecimal.ZERO) != 0;

            boolean codCollectionMismatch =
                    codCollectionDifference.compareTo(BigDecimal.ZERO) != 0;

            boolean codSettlementMismatch =
                    transaction.getSettlementStatus()
                            != CourierSettlementStatus.PENDING
                            && codSettlementDifference.compareTo(
                            BigDecimal.ZERO) != 0;

            int mismatchCount = 0;

            if (weightMismatch) mismatchCount++;
            if (shippingMismatch) mismatchCount++;
            if (codCollectionMismatch) mismatchCount++;
            if (codSettlementMismatch) mismatchCount++;

            CourierReconciliationStatus status;

            if (mismatchCount == 0) {
                status = CourierReconciliationStatus.MATCHED;

            } else if (mismatchCount > 1) {
                status = CourierReconciliationStatus.MULTIPLE_DISCREPANCIES;

            } else if (weightMismatch) {
                status = CourierReconciliationStatus.WEIGHT_MISMATCH;

            } else if (shippingMismatch) {
                status = CourierReconciliationStatus.SHIPPING_CHARGE_MISMATCH;

            } else if (codCollectionMismatch) {
                status = CourierReconciliationStatus.COD_COLLECTION_MISMATCH;

            } else {
                status = CourierReconciliationStatus.COD_SETTLEMENT_MISMATCH;
            }

            CourierReconciliationResponse response =
                    new CourierReconciliationResponse(
                            shipment.getTrackingNumber(),

                            shipment.getExpectedWeight(),
                            transaction.getActualWeight(),
                            weightDifference,

                            shipment.getExpectedShippingCharge(),
                            transaction.getActualShippingCharge(),
                            shippingDifference,

                            shipment.getExpectedCodAmount(),
                            transaction.getCodCollected(),
                            codCollectionDifference,

                            transaction.getCodSettled(),
                            codSettlementDifference,

                            status
                    );

            results.add(response);
        }

        return results;
    }

    // Calculate reconciliation and save history
    public List<CourierReconciliationResponse> runAndSave() {

        List<CourierReconciliationResponse> results = reconcileAll();

        for (CourierReconciliationResponse response : results) {
            saveHistory(response);
        }

        return results;
    }

    // Save one reconciliation result
    private void saveHistory(CourierReconciliationResponse response) {

        CourierReconciliationRecord record =
                new CourierReconciliationRecord();

        record.setTrackingNumber(response.trackingNumber());
        record.setWeightDiscrepancy(response.weightDiscrepancy());
        record.setShippingChargeDiscrepancy(
                response.shippingChargeDiscrepancy());
        record.setCodCollectionDiscrepancy(
                response.codCollectionDiscrepancy());
        record.setCodSettlementDiscrepancy(
                response.codSettlementDiscrepancy());
        record.setStatus(response.status());

        recordService.saveRecord(record);
    }
}
