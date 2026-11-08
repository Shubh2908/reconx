
package com.reconx.backend.service;

import com.reconx.backend.entity.ReconciliationRecord;
import com.reconx.backend.repository.ReconciliationRecordRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReconciliationRecordService {

    private final ReconciliationRecordRepository repository;

    public ReconciliationRecordService(
            ReconciliationRecordRepository repository) {
        this.repository = repository;
    }

    public ReconciliationRecord saveRecord(
            ReconciliationRecord record) {

        record.setReconciledAt(LocalDateTime.now());

        return repository.save(record);
    }

    public List<ReconciliationRecord> getAllHistory() {
        return repository.findAllByOrderByReconciledAtDesc();
    }

    public List<ReconciliationRecord> getHistoryByOrder(
            String orderNumber) {

        return repository.findByOrderNumberOrderByReconciledAtDesc(
                orderNumber);
    }
}
