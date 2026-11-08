
package com.reconx.backend.repository;

import com.reconx.backend.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReconciliationRecordRepository
        extends JpaRepository<ReconciliationRecord, Long> {

    List<ReconciliationRecord>
    findAllByOrderByReconciledAtDesc();

    List<ReconciliationRecord>
    findByOrderNumberOrderByReconciledAtDesc(String orderNumber);
}
