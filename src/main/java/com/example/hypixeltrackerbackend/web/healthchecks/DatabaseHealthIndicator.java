package com.example.hypixeltrackerbackend.web.healthchecks;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.repository.ItemPricingRepository;
import com.example.hypixeltrackerbackend.utils.CollectionsUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component("databaseHealthIndicator")
public class DatabaseHealthIndicator implements HealthIndicator {
    private final ItemPricingRepository itemPricingRepository;
    private static final String TEST_ITEM_ID = "BOOSTER_COOKIE";

    public DatabaseHealthIndicator(ItemPricingRepository itemPricingRepository) {
        this.itemPricingRepository = itemPricingRepository;
    }

    @Override
    public Health health() {
        Health.Builder status = Health.up();
        List<ItemPricing> lastEntries = itemPricingRepository.findAllByItemIdAndLastUpdateBetweenOrderByLastUpdate(TEST_ITEM_ID, LocalDateTime.now().minusMinutes(3), LocalDateTime.now());
        if (CollectionsUtils.isEmpty(lastEntries)) {
            status = Health.down();
        }
        return status.build();
    }
}
