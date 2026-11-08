
package com.reconx.backend.dto;

import com.reconx.backend.entity.FileImportStatus;

import java.time.LocalDateTime;
import java.util.List;

public record FileImportResponse(
        Long id,
        String fileName,
        String fileType,
        Integer totalRecords,
        Integer successfulRecords,
        Integer failedRecords,
        FileImportStatus status,
        LocalDateTime importedAt,
        List<RowError> errors
) {
    public record RowError(
            Integer rowNumber,
            String message
    ) {
    }
}
