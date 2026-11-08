
package com.reconx.backend.repository;

import com.reconx.backend.entity.RefundTransaction;
import com.reconx.backend.entity.RefundStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundTransactionRepository
        extends JpaRepository<RefundTransaction, Long> {

    Optional<RefundTransaction> findByRefundReference(
            String refundReference);

    boolean existsByRefundReference(String refundReference);

    List<RefundTransaction> findAllByReturnReference(
            String returnReference);

    List<RefundTransaction> findAllByOrderNumber(
            String orderNumber);

    List<RefundTransaction> findAllByRefundStatus(
            RefundStatus refundStatus);
}
