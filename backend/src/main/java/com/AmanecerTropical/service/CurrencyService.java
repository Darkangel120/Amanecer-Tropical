package com.AmanecerTropical.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;
    private static final String EXCHANGE_RATE_API_URL = "https://ve.dolarapi.com/v1/dolares";

    public CurrencyService() {
        this.restTemplate = new RestTemplate();
    }

    @Cacheable(value = "usdToVesRate", unless = "#result == null")
    public BigDecimal getUsdToVesRate() {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> response = restTemplate.getForObject(EXCHANGE_RATE_API_URL, List.class);

            if (response != null) {
                for (Map<String, Object> rate : response) {
                    if ("oficial".equals(rate.get("fuente"))) {
                        Object promedioObj = rate.get("promedio");
                        if (promedioObj instanceof Number) {
                            return BigDecimal.valueOf(((Number) promedioObj).doubleValue())
                                    .setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                }
            }
        } catch (RestClientException e) {
            // Log error and return fallback rate
            System.err.println("Error fetching exchange rate: " + e.getMessage());
        }

        // Fallback rate (approximate current rate as of 2024)
        return BigDecimal.valueOf(36.50);
    }

    public BigDecimal convertUsdToVes(BigDecimal usdAmount) {
        BigDecimal rate = getUsdToVesRate();
        return usdAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal convertVesToUsd(BigDecimal vesAmount) {
        BigDecimal rate = getUsdToVesRate();
        return vesAmount.divide(rate, 2, RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> getExchangeInfo() {
        BigDecimal rate = getUsdToVesRate();
        return Map.of(
            "usdToVesRate", rate,
            "vesToUsdRate", BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP)
        );
    }
}

@Configuration
@EnableCaching
class CacheConfig {
}
