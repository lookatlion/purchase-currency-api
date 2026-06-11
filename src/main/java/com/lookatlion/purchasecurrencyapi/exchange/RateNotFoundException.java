package com.lookatlion.purchasecurrencyapi.exchange;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException(UUID purchaseId, String currency) {
        super("Purchase " + purchaseId + " cannot be converted to currency " + currency
                + ": no exchange rate available within 6 months of the transaction date.");
    }
}
