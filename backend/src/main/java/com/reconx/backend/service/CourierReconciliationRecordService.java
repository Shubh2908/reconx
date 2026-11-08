
package com.reconx.backend.service;

import com.reconx.backend.entity.CourierReconciliationRecord;
import com.reconx.backend.repository.CourierReconciliationRecordRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourierReconciliationRecordService {

    private final CourierReconciliationRecordRepository repository;

    public CourierReconciliationRecordService(
            CourierReconciliationRecordRepository repository) {
        this.repository = repository;
    }

    public CourierReconciliationRecord saveRecord(
            CourierReconciliationRecord record) {

        record.setReconciledAt(LocalDateTime.now());

        return repository.save(record);
    }

    public List<CourierReconciliationRecord> getAllHistory() {
        return repository.findAllByOrderByReconciledAtDesc();
    }

    public List<CourierReconciliationRecord> getHistoryByTrackingNumber(
            String trackingNumber) {

        return repository.findByTrackingNumberOrderByReconciledAtDesc(
                trackingNumber);
    }
}
