
package com.reconx.backend.repository;

import com.reconx.backend.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository
        extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingNumber(
            String trackingNumber);

    boolean existsByTrackingNumber(
            String trackingNumber);
}
