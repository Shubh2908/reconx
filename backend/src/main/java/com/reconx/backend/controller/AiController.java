
package com.reconx.backend.controller;

import com.reconx.backend.service.ReconAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final ReconAiService reconAiService;

    public AiController(ReconAiService reconAiService) {
        this.reconAiService = reconAiService;
    }

    @PostMapping("/explain")
    public AiResponse explainDiscrepancy(
            @RequestBody AiRequest request) {

        String explanation = reconAiService.explainDiscrepancy(
                request.recordType(),
                request.reference(),
                request.expectedAmount(),
                request.actualAmount(),
                request.discrepancy(),
                request.additionalDetails()
        );
        return new AiResponse(explanation);
    }

    public record AiRequest(
            String recordType,
            String reference,
            double expectedAmount,
            double actualAmount,
            double discrepancy,
            String additionalDetails
    ) {}

    public record AiResponse(String explanation) {}
}
