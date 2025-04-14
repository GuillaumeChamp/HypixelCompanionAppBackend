package com.example.hypixeltrackerbackend.data.bazaar;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ItemPricingId implements Serializable {
    private String itemId;
    private LocalDateTime time;

    public ItemPricingId(LocalDateTime time, String itemId) {
        this.time = time;
        this.itemId = itemId;
    }

    public ItemPricingId() {
    }
}
