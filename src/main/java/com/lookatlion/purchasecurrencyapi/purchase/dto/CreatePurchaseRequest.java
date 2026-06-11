package com.lookatlion.purchasecurrencyapi.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePurchaseRequest(

        @NotBlank
        @Size(max = 50)
        String description,

        @NotNull
        LocalDate transactionDate,

        @NotNull
        @Positive
        @Digits(integer = 17, fraction = 2)
        BigDecimal amountUsd) {
}
