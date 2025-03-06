package com.example.hypixeltrackerbackend.data.mapper;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ItemPricingMapperTest {
    @Test
    void shouldDataDeserializeProperly() throws IOException {
        String payload = Files.readString(Path.of("src/test/resources/bazaar.json"));
        Map<String, ItemPricing> items = ItemPricingMapper.toBazaarItems(payload);
        assertThat(items).hasSizeLessThan(1350);
    }
}