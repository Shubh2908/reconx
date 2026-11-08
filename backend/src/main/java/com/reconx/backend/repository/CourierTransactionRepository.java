
package com.reconx.backend.repository;

import com.reconx.backend.entity.CourierTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourierTransactionRepository
        extends JpaRepository<CourierTransaction, Long> {

    List<CourierTransaction> findAllByTrackingNumber(
            String trackingNumber);

    boolean existsByCourierReference(
            String courierReference);
}
