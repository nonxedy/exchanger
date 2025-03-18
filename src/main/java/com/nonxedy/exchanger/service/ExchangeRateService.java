package com.nonxedy.exchanger.service;

import com.nonxedy.exchanger.dto.CurrencyDTO;
import com.nonxedy.exchanger.dto.ExchangeRateDTO;
import com.nonxedy.exchanger.entity.Currency;
import com.nonxedy.exchanger.entity.ExchangeRate;
import com.nonxedy.exchanger.exception.DuplicateResourceException;
import com.nonxedy.exchanger.exception.ResourceNotFoundException;
import com.nonxedy.exchanger.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyService currencyService;

    @Autowired
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, CurrencyService currencyService) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyService = currencyService;
    }

    public List<ExchangeRateDTO> getAllExchangeRates() {
        return exchangeRateRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExchangeRateDTO getExchangeRate(String baseCode, String targetCode) {
        Currency baseCurrency = currencyService.findCurrencyByCode(baseCode);
        Currency targetCurrency = currencyService.findCurrencyByCode(targetCode);
        
        ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange rate not found for pair: " + baseCode + "-" + targetCode));
        
        return convertToDTO(exchangeRate);
    }

    public ExchangeRateDTO addExchangeRate(String baseCode, String targetCode, BigDecimal rate) {
        Currency baseCurrency = currencyService.findCurrencyByCode(baseCode);
        Currency targetCurrency = currencyService.findCurrencyByCode(targetCode);
        
        if (exchangeRateRepository.existsByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)) {
            throw new DuplicateResourceException("Exchange rate already exists for pair: " + baseCode + "-" + targetCode);
        }
        
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rate);
        
        ExchangeRate savedExchangeRate = exchangeRateRepository.save(exchangeRate);
        return convertToDTO(savedExchangeRate);
    }

    public ExchangeRateDTO updateExchangeRate(String baseCode, String targetCode, BigDecimal rate) {
        Currency baseCurrency = currencyService.findCurrencyByCode(baseCode);
        Currency targetCurrency = currencyService.findCurrencyByCode(targetCode);
        
        ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange rate not found for pair: " + baseCode + "-" + targetCode));
        
        exchangeRate.setRate(rate);
        ExchangeRate updatedExchangeRate = exchangeRateRepository.save(exchangeRate);
        
        return convertToDTO(updatedExchangeRate);
    }
    
    public BigDecimal findExchangeRate(String fromCurrencyCode, String toCurrencyCode) {
        Currency fromCurrency = currencyService.findCurrencyByCode(fromCurrencyCode);
        Currency toCurrency = currencyService.findCurrencyByCode(toCurrencyCode);
        
        // Сценарий 1: Прямой курс
        Optional<ExchangeRate> directRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(
                fromCurrency, toCurrency);
        
        if (directRate.isPresent()) {
            return directRate.get().getRate();
        }
        
        // Сценарий 2: Обратный курс
        Optional<ExchangeRate> inverseRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(
                toCurrency, fromCurrency);
        
        if (inverseRate.isPresent()) {
            return BigDecimal.ONE.divide(inverseRate.get().getRate(), 6, RoundingMode.HALF_UP);
        }
        
        // Сценарий 3: Кросс-курс через USD
        Currency usdCurrency;
        try {
            usdCurrency = currencyService.findCurrencyByCode("USD");
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Cannot calculate cross rate: USD currency not found");
        }
        
        Optional<ExchangeRate> fromToUsd = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(
                fromCurrency, usdCurrency);
        Optional<ExchangeRate> usdToTarget = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(
                usdCurrency, toCurrency);
        
        if (fromToUsd.isPresent() && usdToTarget.isPresent()) {
            return fromToUsd.get().getRate().multiply(usdToTarget.get().getRate());
        }
        
        Optional<ExchangeRate> usdToFrom = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(
                usdCurrency, fromCurrency);
        
        if (usdToFrom.isPresent() && usdToTarget.isPresent()) {
            return usdToTarget.get().getRate().divide(
                    usdToFrom.get().getRate(), 6, RoundingMode.HALF_UP);
        }
        
        Optional<ExchangeRate> targetToUsd = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(
                toCurrency, usdCurrency);
        
        if (fromToUsd.isPresent() && targetToUsd.isPresent()) {
            return fromToUsd.get().getRate().divide(
                    targetToUsd.get().getRate(), 6, RoundingMode.HALF_UP);
        }
        
        throw new ResourceNotFoundException(
                "Cannot find or calculate exchange rate for pair: " + fromCurrencyCode + "-" + toCurrencyCode);
    }

    private ExchangeRateDTO convertToDTO(ExchangeRate exchangeRate) {
        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setId(exchangeRate.getId());
        
        CurrencyDTO baseCurrencyDTO = new CurrencyDTO();
        baseCurrencyDTO.setId(exchangeRate.getBaseCurrency().getId());
        baseCurrencyDTO.setCode(exchangeRate.getBaseCurrency().getCode());
        baseCurrencyDTO.setName(exchangeRate.getBaseCurrency().getName());
        baseCurrencyDTO.setSign(exchangeRate.getBaseCurrency().getSign());
        
        CurrencyDTO targetCurrencyDTO = new CurrencyDTO();
        targetCurrencyDTO.setId(exchangeRate.getTargetCurrency().getId());
        targetCurrencyDTO.setCode(exchangeRate.getTargetCurrency().getCode());
        targetCurrencyDTO.setName(exchangeRate.getTargetCurrency().getName());
        targetCurrencyDTO.setSign(exchangeRate.getTargetCurrency().getSign());
        
        dto.setBaseCurrency(baseCurrencyDTO);
        dto.setTargetCurrency(targetCurrencyDTO);
        dto.setRate(exchangeRate.getRate());
        
        return dto;
    }
}
