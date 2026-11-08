
package com.reconx.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ReconAiService {

    private final ChatClient chatClient;

    public ReconAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    public String explainDiscrepancy(
            String recordType,
            String reference,
            double expectedAmount,
            double actualAmount,
            double discrepancy,
            String additionalDetails
    ) {
        String prompt = """
            You are ReconX AI, an expert financial reconciliation assistant
            for D2C and e-commerce businesses.

            Analyze the following reconciliation record:

            Record type: %s
            Reference: %s
            Expected amount: %.2f
            Actual amount: %.2f
            Discrepancy: %.2f

            Additional reconciliation details:
            %s

            Explain:
            1. What the discrepancy means.
            2. Possible reasons for each identified mismatch.
            3. Recommended steps for the merchant.
            4. Whether the record requires further investigation.

            For courier records, analyze shipping charges, shipment weight,
            COD expected, COD collected, and COD settled wherever provided.

            Do not assume missing values are zero.
            Do not invent transaction details.
            Do not claim that a payment or settlement has been completed.
            Never recommend marking an unmatched record as matched.
            Keep the explanation clear, concise, and professional.
            """.formatted(
                recordType,
                reference,
                expectedAmount,
                actualAmount,
                discrepancy,
                additionalDetails == null ? "No additional details provided" : additionalDetails
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

}
