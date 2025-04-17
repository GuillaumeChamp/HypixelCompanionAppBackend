package com.example.hypixeltrackerbackend.data.bazaar;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@IdClass(ItemPricingId.class)
@Table(name = "pricing")
public class ItemPricing {
    @Id
    @Column(length = 45)
    private String itemId;
    @Id
    private LocalDateTime time;
    private Double sellPrice;
    private Double buyPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private Double minimalPrice;

    public ItemPricing(String itemId, Double sellPrice, Double buyPrice, LocalDateTime update) {
        this.itemId = itemId;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.time = update;
    }

    public ItemPricing() {}

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
    @JsonProperty("timestamp")
    public LocalDateTime getTime() {
        return time;
    }

    public Double getMinimalPrice() {
        return minimalPrice;
    }

    public void setMinimalPrice(double minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

}
