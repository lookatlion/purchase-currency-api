package com.lookatlion.purchasecurrencyapi.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ConvertedPurchaseResponse(
        UUID id,
        String description,
        LocalDate transactionDate,
        BigDecimal amountUsd,
        String targetCurrency,
        BigDecimal exchangeRate,
        LocalDate exchangeRateDate,
        BigDecimal convertedAmount) {
}
