
package com.reconx.backend.controller;

import com.reconx.backend.dto.ShipmentRequest;
import com.reconx.backend.entity.Shipment;
import com.reconx.backend.service.ShipmentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment createShipment(
            @Valid @RequestBody ShipmentRequest request) {

        return service.createShipment(request);
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return service.getAllShipments();
    }
}
