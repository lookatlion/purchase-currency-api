package com.lookatlion.purchasecurrencyapi.purchase;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(UUID id) {
        super("Purchase not found: " + id);
    }
}
