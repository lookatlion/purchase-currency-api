package com.lookatlion.purchasecurrencyapi.purchase;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lookatlion.purchasecurrencyapi.purchase.dto.CreatePurchaseRequest;
import com.lookatlion.purchasecurrencyapi.purchase.dto.PurchaseResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse create(@Valid @RequestBody CreatePurchaseRequest request) {
        return PurchaseResponse.from(service.create(request));
    }
}
