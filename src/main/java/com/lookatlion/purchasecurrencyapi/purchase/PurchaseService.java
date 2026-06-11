package com.lookatlion.purchasecurrencyapi.purchase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lookatlion.purchasecurrencyapi.purchase.dto.CreatePurchaseRequest;

@Service
public class PurchaseService {

    private final PurchaseRepository repository;

    public PurchaseService(PurchaseRepository repository) {
        this.repository = repository;
    }

    public Purchase create(CreatePurchaseRequest request) {
        Purchase purchase = new Purchase(
                UUID.randomUUID(),
                request.description(),
                request.transactionDate(),
                request.amountUsd());
        return repository.save(purchase);
    }
}
