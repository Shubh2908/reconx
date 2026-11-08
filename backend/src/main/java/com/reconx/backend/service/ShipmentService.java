
package com.reconx.backend.service;

import com.reconx.backend.dto.ShipmentRequest;
import com.reconx.backend.entity.Shipment;
import com.reconx.backend.repository.ShipmentRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;

    public ShipmentService(ShipmentRepository repository) {
        this.repository = repository;
    }

    public Shipment createShipment(ShipmentRequest request) {

        if (repository.existsByTrackingNumber(
                request.getTrackingNumber())) {

            throw new IllegalArgumentException(
                    "Tracking number already exists");
        }

        Shipment shipment = new Shipment();

        shipment.setTrackingNumber(request.getTrackingNumber());
        shipment.setOrderNumber(request.getOrderNumber());
        shipment.setCourierName(request.getCourierName());
        shipment.setExpectedWeight(request.getExpectedWeight());
        shipment.setExpectedShippingCharge(
                request.getExpectedShippingCharge());
        shipment.setExpectedCodAmount(request.getExpectedCodAmount());
        shipment.setShipmentDate(request.getShipmentDate());
        shipment.setShipmentStatus(request.getShipmentStatus());

        return repository.save(shipment);
    }

    public List<Shipment> getAllShipments() {
        return repository.findAll();
    }
}
