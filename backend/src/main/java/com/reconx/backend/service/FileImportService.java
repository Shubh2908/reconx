
package com.reconx.backend.service;

import com.reconx.backend.dto.FileImportResponse;
import com.reconx.backend.entity.*;
import com.reconx.backend.repository.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FileImportService {

    private final FileImportRecordRepository repository;
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final CourierTransactionRepository courierRepository;
    private final ReturnRecordRepository returnRepository;
    private final RefundTransactionRepository refundRepository;

    public FileImportService(
            FileImportRecordRepository repository,
            OrderRepository orderRepository,
            PaymentTransactionRepository paymentRepository,
            ShipmentRepository shipmentRepository,
            CourierTransactionRepository courierRepository,
            ReturnRecordRepository returnRepository,
            RefundTransactionRepository refundRepository) {

        this.repository = repository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
        this.courierRepository = courierRepository;
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
    }

    public FileImportResponse importFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String lowerName = fileName.toLowerCase(Locale.ROOT);

        ExcelData data;

        try {
            if (lowerName.endsWith(".csv")) {
                data = readCsv(file);
            } else if (lowerName.endsWith(".xlsx")) {
                data = readExcel(file);
            } else {
                throw new IllegalArgumentException(
                        "Only CSV and XLSX files are supported");
            }
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Unable to read file: " + exception.getMessage());
        }

        List<Map<String, String>> rows = data.rows();
        Map<String, Integer> headers = data.headers();

        String fileType = detectFileType(headers);

        int successful = 0;
        int failed = 0;

        List<FileImportResponse.RowError> errors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {

            Map<String, String> row = rows.get(i);

            // Row 1 contains headers, so data starts at row 2.
            int rowNumber = i + 2;

            try {
                switch (fileType) {
                    case "ORDERS" -> importOrder(row);
                    case "PAYMENTS" -> importPayment(row);
                    case "COURIER" -> importCourier(row);
                    case "REFUNDS" -> importRefund(row);
                    default -> throw new IllegalArgumentException(
                            "Unsupported import type");
                }

                successful++;

            } catch (Exception exception) {

                failed++;

                String message = exception.getMessage();

                if (message == null || message.isBlank()) {
                    message = "Invalid record";
                }

                errors.add(new FileImportResponse.RowError(
                        rowNumber,
                        message
                ));
            }
        }

        FileImportRecord record = new FileImportRecord();

        record.setFileName(fileName);
        record.setFileType(fileType);
        record.setTotalRecords(rows.size());
        record.setSuccessfulRecords(successful);
        record.setFailedRecords(failed);
        record.setImportedAt(LocalDateTime.now());

        if (rows.isEmpty() || successful == 0) {
            record.setStatus(FileImportStatus.FAILED);
        } else if (failed > 0) {
            record.setStatus(FileImportStatus.PARTIALLY_COMPLETED);
        } else {
            record.setStatus(FileImportStatus.COMPLETED);
        }

        FileImportRecord saved = repository.save(record);

        return new FileImportResponse(
                saved.getId(),
                saved.getFileName(),
                saved.getFileType(),
                saved.getTotalRecords(),
                saved.getSuccessfulRecords(),
                saved.getFailedRecords(),
                saved.getStatus(),
                saved.getImportedAt(),
                errors
        );
    }

    private String detectFileType(Map<String, Integer> headers) {

        if (headers.containsKey("customerName")) {

            validateHeaders(headers, List.of(
                    "orderNumber", "customerName", "customerEmail",
                    "orderDate", "totalAmount", "paymentMethod",
                    "paymentStatus", "orderStatus"
            ));

            return "ORDERS";
        }

        if (headers.containsKey("transactionReference")
                && headers.containsKey("amount")) {

            validateHeaders(headers, List.of(
                    "transactionReference", "orderNumber",
                    "amount", "status", "transactionDate"
            ));

            return "PAYMENTS";
        }

        if (headers.containsKey("courierReference")
                && headers.containsKey("trackingNumber")) {

            validateHeaders(headers, List.of(
                    "courierReference", "trackingNumber",
                    "actualWeight", "actualShippingCharge",
                    "codCollected", "codSettled",
                    "settlementStatus", "transactionDate"
            ));

            return "COURIER";
        }

        if (headers.containsKey("refundReference")
                && headers.containsKey("returnReference")) {

            validateHeaders(headers, List.of(
                    "refundReference", "returnReference",
                    "orderNumber", "refundAmount",
                    "refundStatus", "refundDate"
            ));

            return "REFUNDS";
        }

        throw new IllegalArgumentException(
                "Unrecognized file format or missing required columns");
    }

    // --------------------------------------------------
    // ORDER IMPORT
    // --------------------------------------------------

    private void importOrder(Map<String, String> row) {

        String number = value(row, "orderNumber");

        requireNotBlank(number, "Order number is required");

        if (orderRepository.existsByOrderNumber(number)) {
            throw new IllegalArgumentException(
                    "Duplicate order number: " + number);
        }

        BigDecimal amount = parseAmount(
                value(row, "totalAmount"), "Total amount");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Total amount must be greater than zero");
        }

        Order order = new Order();

        order.setOrderNumber(number);
        order.setCustomerName(value(row, "customerName"));
        order.setCustomerEmail(value(row, "customerEmail"));
        order.setOrderDate(parseDate(value(row, "orderDate")));
        order.setTotalAmount(amount);

        order.setPaymentMethod(PaymentMethod.valueOf(
                value(row, "paymentMethod").toUpperCase(Locale.ROOT)));

        order.setPaymentStatus(PaymentStatus.valueOf(
                value(row, "paymentStatus").toUpperCase(Locale.ROOT)));

        order.setOrderStatus(OrderStatus.valueOf(
                value(row, "orderStatus").toUpperCase(Locale.ROOT)));

        orderRepository.save(order);
    }

    // --------------------------------------------------
    // PAYMENT IMPORT
    // --------------------------------------------------

    private void importPayment(Map<String, String> row) {

        String reference = value(row, "transactionReference");
        String number = value(row, "orderNumber");

        requireNotBlank(reference, "Transaction reference is required");
        requireNotBlank(number, "Order number is required");

        if (paymentRepository.existsByTransactionReference(reference)) {
            throw new IllegalArgumentException(
                    "Duplicate transaction reference: " + reference);
        }

        if (!orderRepository.existsByOrderNumber(number)) {
            throw new IllegalArgumentException(
                    "Order not found: " + number);
        }

        BigDecimal amount = parseAmount(
                value(row, "amount"), "Payment amount");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero");
        }

        PaymentTransaction transaction = new PaymentTransaction();

        transaction.setTransactionReference(reference);
        transaction.setOrderNumber(number);
        transaction.setAmount(amount);

        transaction.setStatus(GatewayPaymentStatus.valueOf(
                value(row, "status").toUpperCase(Locale.ROOT)));

        transaction.setTransactionDate(
                parseDate(value(row, "transactionDate")));

        paymentRepository.save(transaction);
    }

    // --------------------------------------------------
    // COURIER IMPORT
    // --------------------------------------------------

    private void importCourier(Map<String, String> row) {

        String reference = value(row, "courierReference");
        String tracking = value(row, "trackingNumber");

        requireNotBlank(reference, "Courier reference is required");
        requireNotBlank(tracking, "Tracking number is required");

        if (courierRepository.existsByCourierReference(reference)) {
            throw new IllegalArgumentException(
                    "Duplicate courier reference: " + reference);
        }

        if (!shipmentRepository.existsByTrackingNumber(tracking)) {
            throw new IllegalArgumentException(
                    "Shipment not found for tracking number: " + tracking);
        }

        BigDecimal weight = parseAmount(
                value(row, "actualWeight"), "Actual weight");

        BigDecimal shipping = parseAmount(
                value(row, "actualShippingCharge"), "Shipping charge");

        BigDecimal collected = parseAmount(
                value(row, "codCollected"), "COD collected");

        BigDecimal settled = parseAmount(
                value(row, "codSettled"), "COD settled");

        if (weight.compareTo(BigDecimal.ZERO) < 0
                || shipping.compareTo(BigDecimal.ZERO) < 0
                || collected.compareTo(BigDecimal.ZERO) < 0
                || settled.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Courier amounts and weight cannot be negative");
        }

        CourierTransaction transaction = new CourierTransaction();

        transaction.setCourierReference(reference);
        transaction.setTrackingNumber(tracking);
        transaction.setActualWeight(weight);
        transaction.setActualShippingCharge(shipping);
        transaction.setCodCollected(collected);
        transaction.setCodSettled(settled);

        transaction.setSettlementStatus(
                CourierSettlementStatus.valueOf(
                        value(row, "settlementStatus")
                                .toUpperCase(Locale.ROOT)));

        transaction.setTransactionDate(
                parseDate(value(row, "transactionDate")));

        courierRepository.save(transaction);
    }

    // --------------------------------------------------
    // REFUND IMPORT
    // --------------------------------------------------

    private void importRefund(Map<String, String> row) {

        String reference = value(row, "refundReference");
        String returnReference = value(row, "returnReference");
        String number = value(row, "orderNumber");

        requireNotBlank(reference, "Refund reference is required");
        requireNotBlank(returnReference, "Return reference is required");
        requireNotBlank(number, "Order number is required");

        if (refundRepository.existsByRefundReference(reference)) {
            throw new IllegalArgumentException(
                    "Duplicate refund reference: " + reference);
        }

        ReturnRecord returnRecord = returnRepository
                .findByReturnReference(returnReference)
                .orElse(null);

        if (returnRecord == null) {
            throw new IllegalArgumentException(
                    "Return record not found: " + returnReference);
        }

        if (!returnRecord.getOrderNumber().equals(number)) {
            throw new IllegalArgumentException(
                    "Order number does not match the return record");
        }

        BigDecimal amount = parseAmount(
                value(row, "refundAmount"), "Refund amount");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero");
        }

        if (amount.compareTo(
                returnRecord.getExpectedRefundAmount()) > 0) {

            throw new IllegalArgumentException(
                    "Refund amount exceeds expected refund amount");
        }

        RefundTransaction transaction = new RefundTransaction();

        transaction.setRefundReference(reference);
        transaction.setReturnReference(returnReference);
        transaction.setOrderNumber(number);
        transaction.setRefundAmount(amount);

        transaction.setRefundStatus(RefundStatus.valueOf(
                value(row, "refundStatus").toUpperCase(Locale.ROOT)));

        transaction.setRefundDate(
                parseDate(value(row, "refundDate")));

        refundRepository.save(transaction);
    }

    // --------------------------------------------------
    // CSV READER
    // --------------------------------------------------

    private ExcelData readCsv(MultipartFile file) throws Exception {

        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, Integer> headers = new LinkedHashMap<>();

        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .get()
                .parse(new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8))) {

            headers.putAll(parser.getHeaderMap());

            if (headers.isEmpty()) {
                throw new IllegalArgumentException(
                        "CSV file has no headers");
            }

            for (CSVRecord csvRow : parser) {

                Map<String, String> row = new HashMap<>();

                for (String header : headers.keySet()) {
                    row.put(header, csvRow.get(header).trim());
                }

                rows.add(row);
            }
        }

        return new ExcelData(rows, headers);
    }

    // --------------------------------------------------
    // EXCEL READER
    // --------------------------------------------------

    private ExcelData readExcel(MultipartFile file) throws Exception {

        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, Integer> headers = new LinkedHashMap<>();

        try (XSSFWorkbook workbook =
                     new XSSFWorkbook(file.getInputStream())) {

            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException(
                        "Excel workbook contains no sheets");
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new IllegalArgumentException(
                        "Excel sheet has no header row");
            }

            DataFormatter formatter = new DataFormatter();

            FormulaEvaluator evaluator =
                    workbook.getCreationHelper().createFormulaEvaluator();

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {

                Cell cell = headerRow.getCell(i);

                if (cell == null) {
                    continue;
                }

                String header =
                        formatter.formatCellValue(cell, evaluator).trim();

                if (!header.isBlank()) {
                    headers.put(header, i);
                }
            }

            if (headers.isEmpty()) {
                throw new IllegalArgumentException(
                        "Excel sheet has no column headers");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row excelRow = sheet.getRow(i);

                if (excelRow == null) {
                    continue;
                }

                Map<String, String> row = new HashMap<>();
                boolean hasData = false;

                for (Map.Entry<String, Integer> entry :
                        headers.entrySet()) {

                    Cell cell = excelRow.getCell(
                            entry.getValue(),
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    String cellValue =
                            readCell(cell, formatter, evaluator);

                    row.put(entry.getKey(), cellValue);

                    if (!cellValue.isBlank()) {
                        hasData = true;
                    }
                }

                if (hasData) {
                    rows.add(row);
                }
            }
        }

        return new ExcelData(rows, headers);
    }

    // --------------------------------------------------
    // COMMON HELPERS
    // --------------------------------------------------

    private String readCell(
            Cell cell,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {

        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.NUMERIC) {

            if (DateUtil.isCellDateFormatted(cell)) {
                return DateUtil.getLocalDateTime(
                        cell.getNumericCellValue()).toString();
            }

            return BigDecimal.valueOf(cell.getNumericCellValue())
                    .stripTrailingZeros()
                    .toPlainString();
        }

        return formatter.formatCellValue(cell, evaluator).trim();
    }

    private String value(
            Map<String, String> row,
            String column) {

        return row.getOrDefault(column, "").trim();
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private BigDecimal parseAmount(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required");
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid " + fieldName + ": " + value);
        }
    }

    private LocalDateTime parseDate(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Date is required");
        }

        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid date format: " + value
                            + ". Expected yyyy-MM-ddTHH:mm:ss");
        }
    }

    private void validateHeaders(
            Map<String, Integer> headers,
            List<String> requiredHeaders) {

        for (String header : requiredHeaders) {

            if (!headers.containsKey(header)) {
                throw new IllegalArgumentException(
                        "Missing file column: " + header);
            }
        }
    }

    public List<FileImportRecord> getImportHistory() {
        return repository.findAllByOrderByImportedAtDesc();
    }

    private record ExcelData(
            List<Map<String, String>> rows,
            Map<String, Integer> headers) {
    }
}
