package com.example.hypixeltrackerbackend.data.bazaar;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ItemPricingId implements Serializable {
    private String itemId;
    private LocalDateTime time;

    public ItemPricingId(LocalDateTime time, String itemId) {
        this.time = time;
        this.itemId = itemId;
    }

    public ItemPricingId() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemPricingId that = (ItemPricingId) o;
        return Objects.equals(itemId, that.itemId) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, time);
    }
}
