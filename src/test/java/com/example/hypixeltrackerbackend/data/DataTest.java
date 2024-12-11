package com.example.hypixeltrackerbackend.data;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DataTest {
    @Test
    void shouldDataDeserializeProperly() throws IOException {
        String payload = Files.readString(Path.of("src/test/resources/bazaar.json"));
        Map<String , ItemPricing> items = ItemPricingMapper.toBazaarItems(payload);
        assertThat(items).hasSizeLessThan(1350);
    }

    @Test
    void assertStaticDataMapper() throws IOException {
        Map<String ,CompleteItem> items = StaticItemMapper.generate();
        assertThat(items).hasSizeLessThan(1349);
    }

}
