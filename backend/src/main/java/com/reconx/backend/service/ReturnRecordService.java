
package com.reconx.backend.service;

import com.reconx.backend.entity.Order;
import com.reconx.backend.entity.OrderStatus;
import com.reconx.backend.entity.ReturnRecord;
import com.reconx.backend.entity.ReturnStatus;
import com.reconx.backend.repository.OrderRepository;
import com.reconx.backend.repository.ReturnRecordRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReturnRecordService {

    private final ReturnRecordRepository returnRepository;
    private final OrderRepository orderRepository;

    public ReturnRecordService(
            ReturnRecordRepository returnRepository,
            OrderRepository orderRepository) {

        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReturnRecord createReturn(ReturnRecord record) {

        Order order = orderRepository.findByOrderNumber(
                record.getOrderNumber()
        ).orElseThrow(() ->
                new RuntimeException("Order not found: "
                        + record.getOrderNumber())
        );

        if (returnRepository.existsByReturnReference(
                record.getReturnReference())) {
            throw new RuntimeException("Return reference already exists");
        }

        if (record.getExpectedRefundAmount() == null ||
                record.getExpectedRefundAmount().signum() <= 0) {
            throw new RuntimeException(
                    "Expected refund amount must be greater than zero");
        }

        if (record.getExpectedRefundAmount()
                .compareTo(order.getTotalAmount()) > 0) {
            throw new RuntimeException(
                    "Refund amount cannot exceed order amount");
        }

        if (record.getReturnStatus() == null) {
            record.setReturnStatus(ReturnStatus.REQUESTED);
        }

        if (record.getReturnDate() == null) {
            record.setReturnDate(LocalDateTime.now());
        }

        order.setOrderStatus(OrderStatus.RETURNED);

        orderRepository.save(order);

        return returnRepository.save(record);
    }

    public List<ReturnRecord> getAllReturns() {
        return returnRepository.findAllByOrderByReturnDateDesc();
    }

    public ReturnRecord getReturnByReference(String reference) {
        return returnRepository.findByReturnReference(reference)
                .orElseThrow(() ->
                        new RuntimeException("Return not found: " + reference));
    }

    public List<ReturnRecord> getReturnsByOrder(String orderNumber) {
        return returnRepository.findAllByOrderNumber(orderNumber);
    }

    public ReturnRecord updateReturnStatus(
            String reference,
            ReturnStatus newStatus) {

        ReturnRecord record = getReturnByReference(reference);

        record.setReturnStatus(newStatus);

        return returnRepository.save(record);
    }
}
