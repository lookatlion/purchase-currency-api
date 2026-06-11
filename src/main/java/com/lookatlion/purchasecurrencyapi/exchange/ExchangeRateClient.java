package com.lookatlion.purchasecurrencyapi.exchange;

import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateClient {

    /**
     * Finds the most recent exchange rate for the given currency whose record date
     * falls within the inclusive range [startInclusive, endInclusive].
     *
     * @param currency       Treasury {@code country_currency_desc} value, e.g. "Canada-Dollar"
     * @param startInclusive earliest acceptable record date (inclusive)
     * @param endInclusive   latest acceptable record date (inclusive)
     * @return the most recent matching rate, or empty if none exists in the range
     */
    Optional<ExchangeRate> findLatestRate(String currency, LocalDate startInclusive, LocalDate endInclusive);
}
