
package com.reconx.backend.service;

import com.reconx.backend.entity.RefundReconciliationRecord;
import com.reconx.backend.repository.RefundReconciliationRecordRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefundReconciliationRecordService {

    private final RefundReconciliationRecordRepository repository;

    public RefundReconciliationRecordService(
            RefundReconciliationRecordRepository repository) {
        this.repository = repository;
    }

    public RefundReconciliationRecord saveRecord(
            RefundReconciliationRecord record) {

        record.setReconciledAt(LocalDateTime.now());
        return repository.save(record);
    }

    public List<RefundReconciliationRecord> getAllHistory() {
        return repository.findAllByOrderByReconciledAtDesc();
    }

    public List<RefundReconciliationRecord> getHistoryByReturnReference(
            String returnReference) {

        return repository.findByReturnReferenceOrderByReconciledAtDesc(
                returnReference);
    }
}
