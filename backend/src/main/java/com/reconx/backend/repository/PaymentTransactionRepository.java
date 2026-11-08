
package com.reconx.backend.repository;

import com.reconx.backend.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findAllByOrderNumber(
            String orderNumber);

    boolean existsByTransactionReference(
            String transactionReference);
}
