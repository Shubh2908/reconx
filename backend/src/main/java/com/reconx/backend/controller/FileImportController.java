
package com.reconx.backend.controller;

import com.reconx.backend.dto.FileImportResponse;
import com.reconx.backend.entity.FileImportRecord;
import com.reconx.backend.service.FileImportService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/imports")
public class FileImportController {

    private final FileImportService fileImportService;

    public FileImportController(FileImportService fileImportService) {
        this.fileImportService = fileImportService;
    }

    // Upload CSV or Excel file
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FileImportResponse uploadFile(
            @RequestParam("file") MultipartFile file) {

        return fileImportService.importFile(file);
    }

    // View import history
    @GetMapping("/history")
    public List<FileImportRecord> getImportHistory() {
        return fileImportService.getImportHistory();
    }
}
