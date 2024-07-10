package com.example.hypixeltrackerbackend.data;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pricing")
public class ItemPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String itemId;
    private Double sellPrice;
    private Double buyPrice;
    private LocalDateTime lastUpdate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private Double minimalPrice;

    public ItemPricing(String itemId, Double sellPrice, Double buyPrice, LocalDateTime update) {
        this.itemId = itemId;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.lastUpdate = update;
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
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public Double getMinimalPrice() {
        return minimalPrice;
    }

    public void setMinimalPrice(double minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
