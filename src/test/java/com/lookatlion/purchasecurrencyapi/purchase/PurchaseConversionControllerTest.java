package com.lookatlion.purchasecurrencyapi.purchase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lookatlion.purchasecurrencyapi.exchange.ExchangeRate;
import com.lookatlion.purchasecurrencyapi.exchange.ExchangeRateClient;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExchangeRateClient exchangeRateClient;

    private String createPurchase(String amountUsd) throws Exception {
        String body = """
                {
                  "description": "Coffee beans",
                  "transactionDate": "2026-01-15",
                  "amountUsd": %s
                }
                """.formatted(amountUsd);

        String created = mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode id = objectMapper.readTree(created).get("id");
        return id.asText();
    }

    @Test
    void convertsPurchaseToTargetCurrency() throws Exception {
        when(exchangeRateClient.findLatestRate(eq("Canada-Dollar"), any(), any()))
                .thenReturn(Optional.of(new ExchangeRate(
                        new BigDecimal("1.35"), LocalDate.parse("2025-12-31"))));

        String id = createPurchase("100.00");

        mockMvc.perform(get("/api/purchases/{id}/conversion", id)
                        .param("currency", "Canada-Dollar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.description").value("Coffee beans"))
                .andExpect(jsonPath("$.transactionDate").value("2026-01-15"))
                .andExpect(jsonPath("$.amountUsd").value(100.00))
                .andExpect(jsonPath("$.targetCurrency").value("Canada-Dollar"))
                .andExpect(jsonPath("$.exchangeRate").value(1.35))
                .andExpect(jsonPath("$.exchangeRateDate").value("2025-12-31"))
                .andExpect(jsonPath("$.convertedAmount").value(135.00));
    }

    @Test
    void returnsNotFoundWhenPurchaseDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/purchases/{id}/conversion", UUID.randomUUID())
                        .param("currency", "Canada-Dollar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsUnprocessableWhenNoRateWithinSixMonths() throws Exception {
        when(exchangeRateClient.findLatestRate(eq("Canada-Dollar"), any(), any()))
                .thenReturn(Optional.empty());

        String id = createPurchase("100.00");

        mockMvc.perform(get("/api/purchases/{id}/conversion", id)
                        .param("currency", "Canada-Dollar"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void roundsConvertedAmountToTwoDecimals() throws Exception {
        // 1.00 * 1.005 = 1.005 -> HALF_UP -> 1.01
        when(exchangeRateClient.findLatestRate(eq("Canada-Dollar"), any(), any()))
                .thenReturn(Optional.of(new ExchangeRate(
                        new BigDecimal("1.005"), LocalDate.parse("2025-12-31"))));

        String id = createPurchase("1.00");

        mockMvc.perform(get("/api/purchases/{id}/conversion", id)
                        .param("currency", "Canada-Dollar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(1.01));
    }
}
