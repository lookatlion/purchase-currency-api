package com.lookatlion.purchasecurrencyapi.purchase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private ObjectMapper objectMapper;

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
    void retrievesStoredPurchaseById() throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "transactionDate": "2026-01-15",
                  "amountUsd": 12.34
                }
                """;

        String created = mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode id = objectMapper.readTree(created).get("id");

        mockMvc.perform(get("/api/purchases/{id}", id.asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.asText()))
                .andExpect(jsonPath("$.description").value("Coffee beans"))
                .andExpect(jsonPath("$.transactionDate").value("2026-01-15"))
                .andExpect(jsonPath("$.amountUsd").value(12.34));
    }

    @Test
    void returnsNotFoundForUnknownId() throws Exception {
        UUID unknownId = UUID.randomUUID();

        mockMvc.perform(get("/api/purchases/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/purchases/" + unknownId))
                .andExpect(jsonPath("$.message", containsString("not found")));
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("description")));
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
