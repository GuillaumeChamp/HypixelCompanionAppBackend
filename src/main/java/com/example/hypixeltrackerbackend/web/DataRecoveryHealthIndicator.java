package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.services.ApiFetcherService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("hypixelApiCallerHealthIndicator")
public class DataRecoveryHealthIndicator implements HealthIndicator {
    private final ApiFetcherService apiFetcherService;

    public DataRecoveryHealthIndicator(ApiFetcherService apiFetcherService) {
        this.apiFetcherService = apiFetcherService;
    }

    @Override
    public Health health() {
        Health.Builder status = Health.up();
        LocalDateTime lastUpdate = apiFetcherService.getLastBazaarAnswer();
        if (lastUpdate == null || lastUpdate.isBefore(LocalDateTime.now().minusMinutes(3))) {
            status = Health.down();
        }
        return status.build();
    }
}
