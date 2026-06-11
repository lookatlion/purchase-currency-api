package com.lookatlion.purchasecurrencyapi.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.lookatlion.purchasecurrencyapi.purchase.Purchase;

public record PurchaseResponse(
        UUID id,
        String description,
        LocalDate transactionDate,
        BigDecimal amountUsd) {

    public static PurchaseResponse from(Purchase purchase) {
        return new PurchaseResponse(
                purchase.getId(),
                purchase.getDescription(),
                purchase.getTransactionDate(),
                purchase.getAmountUsd());
    }
}
