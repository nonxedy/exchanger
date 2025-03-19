package com.nonxedy.exchanger.repository;

import com.nonxedy.exchanger.entity.Currency;
import com.nonxedy.exchanger.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
    boolean existsByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
}
