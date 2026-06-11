package com.lookatlion.purchasecurrencyapi.purchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lookatlion.purchasecurrencyapi.exchange.ExchangeRate;
import com.lookatlion.purchasecurrencyapi.exchange.ExchangeRateClient;
import com.lookatlion.purchasecurrencyapi.exchange.RateNotFoundException;
import com.lookatlion.purchasecurrencyapi.purchase.dto.ConvertedPurchaseResponse;
import com.lookatlion.purchasecurrencyapi.purchase.dto.CreatePurchaseRequest;

@Service
public class PurchaseService {

    private static final int RATE_WINDOW_MONTHS = 6;
    private static final int CURRENCY_SCALE = 2;

    private final PurchaseRepository repository;
    private final ExchangeRateClient exchangeRateClient;

    public PurchaseService(PurchaseRepository repository, ExchangeRateClient exchangeRateClient) {
        this.repository = repository;
        this.exchangeRateClient = exchangeRateClient;
    }

    public Purchase create(CreatePurchaseRequest request) {
        Purchase purchase = new Purchase(
                UUID.randomUUID(),
                request.description(),
                request.transactionDate(),
                request.amountUsd());
        return repository.save(purchase);
    }

    public Purchase getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PurchaseNotFoundException(id));
    }

    public ConvertedPurchaseResponse convert(UUID id, String currency) {
        Purchase purchase = getById(id);

        LocalDate end = purchase.getTransactionDate();
        LocalDate start = end.minusMonths(RATE_WINDOW_MONTHS);

        ExchangeRate rate = exchangeRateClient.findLatestRate(currency, start, end)
                .orElseThrow(() -> new RateNotFoundException(id, currency));

        BigDecimal convertedAmount = purchase.getAmountUsd()
                .multiply(rate.rate())
                .setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        return new ConvertedPurchaseResponse(
                purchase.getId(),
                purchase.getDescription(),
                purchase.getTransactionDate(),
                purchase.getAmountUsd(),
                currency,
                rate.rate(),
                rate.recordDate(),
                convertedAmount);
    }
}
