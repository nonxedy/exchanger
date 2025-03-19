package com.nonxedy.exchanger.service;

import com.nonxedy.exchanger.dto.CurrencyDTO;
import com.nonxedy.exchanger.dto.ExchangeResponseDTO;
import com.nonxedy.exchanger.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeService {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;

    @Autowired
    public ExchangeService(ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
    }

    public ExchangeResponseDTO calculateExchange(String fromCurrencyCode, String toCurrencyCode, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be a positive number");
        }

        CurrencyDTO fromCurrency = currencyService.getCurrencyByCode(fromCurrencyCode);
        CurrencyDTO toCurrency = currencyService.getCurrencyByCode(toCurrencyCode);
        
        BigDecimal rate = exchangeRateService.findExchangeRate(fromCurrencyCode, toCurrencyCode);
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        
        ExchangeResponseDTO response = new ExchangeResponseDTO();
        response.setBaseCurrency(fromCurrency);
        response.setTargetCurrency(toCurrency);
        response.setRate(rate);
        response.setAmount(amount);
        response.setConvertedAmount(convertedAmount);
        
        return response;
    }
}
