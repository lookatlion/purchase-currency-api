package com.lookatlion.purchasecurrencyapi.exchange;

import java.util.UUID;

public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException(UUID purchaseId, String currency) {
        super("Purchase " + purchaseId + " cannot be converted to currency " + currency
                + ": no exchange rate available within 6 months of the transaction date.");
    }
}
