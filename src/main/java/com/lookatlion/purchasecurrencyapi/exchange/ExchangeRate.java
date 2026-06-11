package com.lookatlion.purchasecurrencyapi.exchange;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRate(BigDecimal rate, LocalDate recordDate) {
}
