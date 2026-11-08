
package com.reconx.backend.service;

import com.reconx.backend.dto.CourierTransactionRequest;
import com.reconx.backend.entity.CourierTransaction;
import com.reconx.backend.repository.CourierTransactionRepository;
import com.reconx.backend.repository.ShipmentRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierTransactionService {

    private final CourierTransactionRepository transactionRepository;
    private final ShipmentRepository shipmentRepository;

    public CourierTransactionService(
            CourierTransactionRepository transactionRepository,
            ShipmentRepository shipmentRepository) {

        this.transactionRepository = transactionRepository;
        this.shipmentRepository = shipmentRepository;
    }

    public CourierTransaction createTransaction(
            CourierTransactionRequest request) {

        if (transactionRepository.existsByCourierReference(
                request.getCourierReference())) {

            throw new IllegalArgumentException(
                    "Courier reference already exists");
        }

        if (shipmentRepository.findByTrackingNumber(
                request.getTrackingNumber()).isEmpty()) {

            throw new IllegalArgumentException(
                    "Shipment not found for tracking number");
        }

        CourierTransaction transaction = new CourierTransaction();

        transaction.setCourierReference(
                request.getCourierReference());

        transaction.setTrackingNumber(
                request.getTrackingNumber());

        transaction.setActualWeight(request.getActualWeight());

        transaction.setActualShippingCharge(
                request.getActualShippingCharge());

        transaction.setCodCollected(request.getCodCollected());

        transaction.setCodSettled(request.getCodSettled());

        transaction.setSettlementStatus(
                request.getSettlementStatus());

        transaction.setTransactionDate(request.getTransactionDate());

        return transactionRepository.save(transaction);
    }

    public List<CourierTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
