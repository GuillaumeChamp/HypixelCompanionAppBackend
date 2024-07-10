package com.example.hypixeltrackerbackend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class PricingRecord {
    private final Double sellPrice;
    private final Double buyPrice;
    private final LocalDateTime lastUpdate;
    public PricingRecord(ItemPricing itemPricing){
        this.sellPrice = itemPricing.getSellPrice();
        this.buyPrice = itemPricing.getBuyPrice();
        this.lastUpdate = itemPricing.getLastUpdate();
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    @JsonProperty("timestamp")
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
