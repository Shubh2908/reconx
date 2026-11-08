
package com.reconx.backend.repository;

import com.reconx.backend.entity.CourierReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourierReconciliationRecordRepository
        extends JpaRepository<CourierReconciliationRecord, Long> {

    List<CourierReconciliationRecord>
    findAllByOrderByReconciledAtDesc();

    List<CourierReconciliationRecord>
    findByTrackingNumberOrderByReconciledAtDesc(
            String trackingNumber);
}
