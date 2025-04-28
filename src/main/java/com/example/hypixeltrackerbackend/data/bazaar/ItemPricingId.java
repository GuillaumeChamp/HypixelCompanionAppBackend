package com.example.hypixeltrackerbackend.data.bazaar;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ItemPricingId implements Serializable {
    private String itemId;
    private LocalDateTime timestamp;

    @SuppressWarnings("unused")
    public ItemPricingId(LocalDateTime timestamp, String itemId) {
        this.timestamp = timestamp;
        this.itemId = itemId;
    }

    @SuppressWarnings("unused")
    public ItemPricingId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemPricingId that = (ItemPricingId) o;
        return Objects.equals(itemId, that.itemId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, timestamp);
    }
}
