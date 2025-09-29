package com.FinTrace.smartWallet.CustomerService.integration;

import com.FinTrace.smartWallet.CustomerService.dto.CurrencyResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(
        prefix = "api.currency",
        name = "mock",
        havingValue = "true",
        matchIfMissing = true
)
public class MockCurrencyClient implements CurrencyClient {

    @Override
    public CurrencyResponse getLatestRates(String apiKey, String baseCurrency) {
        // Mock implementation returning hardcoded values
        CurrencyResponse response = new CurrencyResponse();
        response.setData(Map.of(
                "USD", 1.0,
                "EUR", 1.0,
                "GBP", 1.0
        ));
        return response;
    }
}
