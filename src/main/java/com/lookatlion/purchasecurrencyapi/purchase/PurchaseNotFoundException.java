package com.lookatlion.purchasecurrencyapi.purchase;

import java.util.UUID;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(UUID id) {
        super("Purchase not found: " + id);
    }
}
