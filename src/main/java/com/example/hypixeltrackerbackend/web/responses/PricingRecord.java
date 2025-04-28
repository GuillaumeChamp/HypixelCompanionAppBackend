package com.example.hypixeltrackerbackend.web.responses;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;

import java.time.LocalDateTime;

public record PricingRecord (Double sellPrice, Double buyPrice, LocalDateTime timestamp){
    public PricingRecord(ItemPricing pricing){
        this(pricing.getSellPrice(),pricing.getBuyPrice(),pricing.getTimestamp());
    }
}

