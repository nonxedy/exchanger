package com.nonxedy.exchanger.controller;

import com.nonxedy.exchanger.dto.ExchangeRateDTO;
import com.nonxedy.exchanger.exception.BadRequestException;
import com.nonxedy.exchanger.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRateDTO>> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRateService.getAllExchangeRates());
    }

    @GetMapping("/exchangeRate/{currencyPair}")
    public ResponseEntity<ExchangeRateDTO> getExchangeRate(@PathVariable String currencyPair) {
        if (currencyPair == null || currencyPair.length() != 6) {
            throw new BadRequestException("Currency pair must be in format USDEUR");
        }
        
        String baseCode = currencyPair.substring(0, 3);
        String targetCode = currencyPair.substring(3, 6);
        
        return ResponseEntity.ok(exchangeRateService.getExchangeRate(baseCode, targetCode));
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRateDTO> addExchangeRate(@RequestBody Map<String, String> request) {
        String baseCode = request.get("baseCurrencyCode");
        String targetCode = request.get("targetCurrencyCode");
        String rateStr = request.get("rate");
        
        if (baseCode == null || targetCode == null || rateStr == null) {
            throw new BadRequestException("baseCurrencyCode, targetCurrencyCode and rate are required");
        }
        
        BigDecimal rate;
        try {
            rate = new BigDecimal(rateStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Rate must be a valid number");
        }
        
        ExchangeRateDTO savedExchangeRate = exchangeRateService.addExchangeRate(baseCode, targetCode, rate);
        return new ResponseEntity<>(savedExchangeRate, HttpStatus.CREATED);
    }

    @PatchMapping("/exchangeRate/{currencyPair}")
    public ResponseEntity<ExchangeRateDTO> updateExchangeRate(
            @PathVariable String currencyPair,
            @RequestBody Map<String, String> request) {
        
        if (currencyPair == null || currencyPair.length() != 6) {
            throw new BadRequestException("Currency pair must be in format USDEUR");
        }
        
        String baseCode = currencyPair.substring(0, 3);
        String targetCode = currencyPair.substring(3, 6);
        
        String rateStr = request.get("rate");
        if (rateStr == null) {
            throw new BadRequestException("Rate is required");
        }
        
        BigDecimal rate;
        try {
            rate = new BigDecimal(rateStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Rate must be a valid number");
        }
        
        ExchangeRateDTO updatedExchangeRate = exchangeRateService.updateExchangeRate(baseCode, targetCode, rate);
        return ResponseEntity.ok(updatedExchangeRate);
    }
}
