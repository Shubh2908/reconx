package com.reconx.backend.repository;

import com.reconx.backend.entity.ReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnRecordRepository
        extends JpaRepository<ReturnRecord, Long> {

    Optional<ReturnRecord> findByReturnReference(String returnReference);

    boolean existsByReturnReference(String returnReference);

    List<ReturnRecord> findAllByOrderNumber(String orderNumber);

    List<ReturnRecord> findAllByOrderByReturnDateDesc();
}