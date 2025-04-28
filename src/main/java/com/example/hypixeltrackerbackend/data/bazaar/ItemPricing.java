package com.example.hypixeltrackerbackend.data.bazaar;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@IdClass(ItemPricingId.class)
@Table(name = "pricing")
public class ItemPricing {
    /**
     * Identifier of the item as provided by Hypixel. Current longest id is 38
     */
    @Id
    @Column(length = 45)
    private String itemId;
    @Id
    private LocalDateTime timestamp;
    private Double sellPrice;
    private Double buyPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private Double minimalPrice;

    public ItemPricing(String itemId, Double sellPrice, Double buyPrice, LocalDateTime update) {
        this.itemId = itemId;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.timestamp = update;
    }

    public ItemPricing() {
    }

    public String getItemId() {
        return itemId;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    @JsonIgnore
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Double getMinimalPrice() {
        return minimalPrice;
    }

    public void setMinimalPrice(double minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

}
