package com.FinTrace.customerSystem.integration;

import com.FinTrace.customerSystem.dto.CurrencyResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencyClient",
        url = "https://api.freecurrencyapi.com",
        contextId = "currencyClient")
@ConditionalOnProperty(
        prefix = "api.currency",
        name = "mock",
        havingValue = "false",
        matchIfMissing = true
)
public interface CurrencyClient {

    @GetMapping("/v1/latest")
    CurrencyResponse getLatestRates(@RequestParam("apikey") String apiKey,
                                    @RequestParam("base_currency") String baseCurrency);

}
