package com.nonxedy.exchanger.service;

import com.nonxedy.exchanger.dto.CurrencyDTO;
import com.nonxedy.exchanger.entity.Currency;
import com.nonxedy.exchanger.exception.DuplicateResourceException;
import com.nonxedy.exchanger.exception.ResourceNotFoundException;
import com.nonxedy.exchanger.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<CurrencyDTO> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CurrencyDTO getCurrencyByCode(String code) {
        Currency currency = findCurrencyByCode(code);
        return convertToDTO(currency);
    }

    public Currency findCurrencyByCode(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with code: " + code));
    }

    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        if (currencyRepository.existsByCode(currencyDTO.getCode())) {
            throw new DuplicateResourceException("Currency with code " + currencyDTO.getCode() + " already exists");
        }
        
        Currency currency = new Currency();
        currency.setCode(currencyDTO.getCode());
        currency.setName(currencyDTO.getName());
        currency.setSign(currencyDTO.getSign());
        
        Currency savedCurrency = currencyRepository.save(currency);
        return convertToDTO(savedCurrency);
    }

    private CurrencyDTO convertToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setId(currency.getId());
        dto.setCode(currency.getCode());
        dto.setName(currency.getName());
        dto.setSign(currency.getSign());
        return dto;
    }
}
