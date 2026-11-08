
package com.reconx.backend.repository;

import com.reconx.backend.entity.FileImportRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileImportRecordRepository
        extends JpaRepository<FileImportRecord, Long> {

    List<FileImportRecord> findAllByOrderByImportedAtDesc();
}
