package com.nonxedy.exchanger.controller;

import com.nonxedy.exchanger.dto.ExchangeResponseDTO;
import com.nonxedy.exchanger.exception.BadRequestException;
import com.nonxedy.exchanger.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class ExchangeController {

    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/exchange")
    public ResponseEntity<ExchangeResponseDTO> exchange(
            @RequestParam("from") String fromCurrencyCode,
            @RequestParam("to") String toCurrencyCode,
            @RequestParam("amount") String amountStr) {
        
        if (fromCurrencyCode == null || toCurrencyCode == null || amountStr == null) {
            throw new BadRequestException("From currency, to currency and amount are required");
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Amount must be a valid number");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
        
        ExchangeResponseDTO response = exchangeService.calculateExchange(fromCurrencyCode, toCurrencyCode, amount);
        return ResponseEntity.ok(response);
    }
}
