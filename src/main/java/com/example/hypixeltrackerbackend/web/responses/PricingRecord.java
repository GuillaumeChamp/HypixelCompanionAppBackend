package com.example.hypixeltrackerbackend.web.responses;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;

import java.time.LocalDateTime;

/**
 * This object is used to describe a pricing record as needed to print it in a graph.
 * @see com.example.hypixeltrackerbackend.web.RequestController#getHistory(String, String)
 * @param sellPrice the sell price
 * @param buyPrice the buy price
 * @param timestamp the timestamp
 */
public record PricingRecord (Double sellPrice, Double buyPrice, LocalDateTime timestamp){
    public PricingRecord(ItemPricing pricing){
        this(pricing.getSellPrice(),pricing.getBuyPrice(),pricing.getTimestamp());
    }
}

