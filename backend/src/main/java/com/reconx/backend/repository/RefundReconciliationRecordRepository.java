
package com.reconx.backend.repository;

import com.reconx.backend.entity.RefundReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundReconciliationRecordRepository
        extends JpaRepository<RefundReconciliationRecord, Long> {

    List<RefundReconciliationRecord>
    findAllByOrderByReconciledAtDesc();

    List<RefundReconciliationRecord>
    findByReturnReferenceOrderByReconciledAtDesc(
            String returnReference);
}
