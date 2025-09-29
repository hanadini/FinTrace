package com.FinTrace.smartWallet.CustomerService.service;

import com.FinTrace.smartWallet.CustomerService.integration.CurrencyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {
    private final CurrencyClient currencyClient;

    @Value( "${currency.api.key}" )
    private String apiKey;

    @Autowired
    public CurrencyService(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    public Double getExchangeRate(String baseCurrency, String targetCurrency) {
        return currencyClient.getLatestRate(apiKey, baseCurrency)
                .getData()
                .get(targetCurrency);
    }
}
