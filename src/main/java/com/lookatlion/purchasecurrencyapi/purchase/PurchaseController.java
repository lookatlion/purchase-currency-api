package com.lookatlion.purchasecurrencyapi.purchase;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lookatlion.purchasecurrencyapi.purchase.dto.ConvertedPurchaseResponse;
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

    @GetMapping("/{id}")
    public PurchaseResponse getById(@PathVariable UUID id) {
        return PurchaseResponse.from(service.getById(id));
    }

    @GetMapping("/{id}/conversion")
    public ConvertedPurchaseResponse convert(@PathVariable UUID id, @RequestParam String currency) {
        return service.convert(id, currency);
    }
}
