
package com.reconx.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_import_records")
public class FileImportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    private String fileType;

    private Integer totalRecords;

    private Integer successfulRecords;

    private Integer failedRecords;

    @Enumerated(EnumType.STRING)
    private FileImportStatus status;

    private LocalDateTime importedAt;

    public FileImportRecord() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getSuccessfulRecords() { return successfulRecords; }
    public void setSuccessfulRecords(Integer successfulRecords) {
        this.successfulRecords = successfulRecords;
    }

    public Integer getFailedRecords() { return failedRecords; }
    public void setFailedRecords(Integer failedRecords) {
        this.failedRecords = failedRecords;
    }

    public FileImportStatus getStatus() { return status; }
    public void setStatus(FileImportStatus status) {
        this.status = status;
    }

    public LocalDateTime getImportedAt() { return importedAt; }
    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }
}
