package com.lookatlion.purchasecurrencyapi.purchase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsPurchaseAndReturnsItWithId() throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "transactionDate": "2026-01-15",
                  "amountUsd": 12.34
                }
                """;

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description").value("Coffee beans"))
                .andExpect(jsonPath("$.transactionDate").value("2026-01-15"))
                .andExpect(jsonPath("$.amountUsd").value(12.34));
    }

    @Test
    void rejectsBlankDescription() throws Exception {
        String body = """
                {
                  "description": "",
                  "transactionDate": "2026-01-15",
                  "amountUsd": 12.34
                }
                """;

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDescriptionLongerThan50Chars() throws Exception {
        String longDescription = "x".repeat(51);
        String body = """
                {
                  "description": "%s",
                  "transactionDate": "2026-01-15",
                  "amountUsd": 12.34
                }
                """.formatted(longDescription);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingTransactionDate() throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "amountUsd": 12.34
                }
                """;

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingAmount() throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "transactionDate": "2026-01-15"
                }
                """;

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNegativeAmount() throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "transactionDate": "2026-01-15",
                  "amountUsd": -1.00
                }
                """;

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAmountWithMoreThanTwoDecimalPlaces() throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "transactionDate": "2026-01-15",
                  "amountUsd": 12.345
                }
                """;

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
