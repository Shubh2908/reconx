
package com.reconx.backend.controller;

import com.reconx.backend.entity.ReturnRecord;
import com.reconx.backend.entity.ReturnStatus;
import com.reconx.backend.service.ReturnRecordService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnRecordController {

    private final ReturnRecordService returnRecordService;

    public ReturnRecordController(
            ReturnRecordService returnRecordService) {
        this.returnRecordService = returnRecordService;
    }

    // Create a new return request
    @PostMapping
    public ReturnRecord createReturn(@RequestBody ReturnRecord record) {
        return returnRecordService.createReturn(record);
    }

    // Get all returns
    @GetMapping
    public List<ReturnRecord> getAllReturns() {
        return returnRecordService.getAllReturns();
    }

    // Get return by reference
    @GetMapping("/{reference}")
    public ReturnRecord getReturnByReference(
            @PathVariable String reference) {
        return returnRecordService.getReturnByReference(reference);
    }

    // Get returns for a particular order
    @GetMapping("/order/{orderNumber}")
    public List<ReturnRecord> getReturnsByOrder(
            @PathVariable String orderNumber) {
        return returnRecordService.getReturnsByOrder(orderNumber);
    }

    // Update return status
    @PutMapping("/{reference}/status")
    public ReturnRecord updateReturnStatus(
            @PathVariable String reference,
            @RequestParam ReturnStatus status) {

        return returnRecordService.updateReturnStatus(
                reference, status);
    }
}
