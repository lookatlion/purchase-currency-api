package com.lookatlion.purchasecurrencyapi.exchange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TreasuryExchangeRateClient implements ExchangeRateClient {

    private static final String RATES_PATH = "/v1/accounting/od/rates_of_exchange";

    private final RestClient restClient;

    public TreasuryExchangeRateClient(RestClient.Builder builder,
            @Value("${treasury.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<ExchangeRate> findLatestRate(String currency, LocalDate startInclusive, LocalDate endInclusive) {
        String filter = "country_currency_desc:eq:" + currency
                + ",record_date:gte:" + startInclusive
                + ",record_date:lte:" + endInclusive;

        TreasuryResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(RATES_PATH)
                        .queryParam("fields", "exchange_rate,record_date")
                        .queryParam("filter", filter)
                        .queryParam("sort", "-record_date")
                        .queryParam("page[size]", "1")
                        .build())
                .retrieve()
                .body(TreasuryResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            return Optional.empty();
        }

        TreasuryRate rate = response.data().get(0);
        return Optional.of(new ExchangeRate(
                new BigDecimal(rate.exchangeRate()),
                LocalDate.parse(rate.recordDate())));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TreasuryResponse(List<TreasuryRate> data) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TreasuryRate(
            @JsonProperty("exchange_rate") String exchangeRate,
            @JsonProperty("record_date") String recordDate) {
    }
}
