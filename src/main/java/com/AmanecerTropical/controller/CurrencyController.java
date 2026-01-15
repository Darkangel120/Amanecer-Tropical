package com.AmanecerTropical.controller;

import com.AmanecerTropical.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/rate")
    public ResponseEntity<Map<String, BigDecimal>> getExchangeRate() {
        try {
            Map<String, BigDecimal> exchangeInfo = currencyService.getExchangeInfo();
            return ResponseEntity.ok(exchangeInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/convert/usd-to-ves")
    public ResponseEntity<BigDecimal> convertUsdToVes(@RequestParam BigDecimal amount) {
        try {
            BigDecimal convertedAmount = currencyService.convertUsdToVes(amount);
            return ResponseEntity.ok(convertedAmount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/convert/ves-to-usd")
    public ResponseEntity<BigDecimal> convertVesToUsd(@RequestParam BigDecimal amount) {
        try {
            BigDecimal convertedAmount = currencyService.convertVesToUsd(amount);
            return ResponseEntity.ok(convertedAmount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertCurrency(@RequestBody Map<String, Object> request) {
        try {
            String fromCurrency = (String) request.get("fromCurrency");
            String toCurrency = (String) request.get("toCurrency");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            BigDecimal result = BigDecimal.ZERO;

            if ("USD".equalsIgnoreCase(fromCurrency) && "VES".equalsIgnoreCase(toCurrency)) {
                result = currencyService.convertUsdToVes(amount);
            } else if ("VES".equalsIgnoreCase(fromCurrency) && "USD".equalsIgnoreCase(toCurrency)) {
                result = currencyService.convertVesToUsd(amount);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Unsupported currency conversion"));
            }

            return ResponseEntity.ok(Map.of(
                "originalAmount", amount,
                "convertedAmount", result,
                "fromCurrency", fromCurrency.toUpperCase(),
                "toCurrency", toCurrency.toUpperCase(),
                "exchangeRate", currencyService.getUsdToVesRate()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
