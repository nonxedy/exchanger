package com.nonxedy.exchanger.controller;

import com.nonxedy.exchanger.dto.CurrencyDTO;
import com.nonxedy.exchanger.exception.BadRequestException;
import com.nonxedy.exchanger.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ResponseEntity<List<CurrencyDTO>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDTO> getCurrencyByCode(@PathVariable String code) {
        if (code == null || code.isEmpty()) {
            throw new BadRequestException("Currency code is required");
        }
        return ResponseEntity.ok(currencyService.getCurrencyByCode(code));
    }

    @PostMapping
    public ResponseEntity<CurrencyDTO> addCurrency(@RequestBody CurrencyDTO currencyDTO) {
        if (currencyDTO.getCode() == null || currencyDTO.getName() == null || currencyDTO.getSign() == null) {
            throw new BadRequestException("Currency code, name and sign are required");
        }
        CurrencyDTO savedCurrency = currencyService.addCurrency(currencyDTO);
        return new ResponseEntity<>(savedCurrency, HttpStatus.CREATED);
    }
}
