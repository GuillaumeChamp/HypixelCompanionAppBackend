package com.example.hypixeltrackerbackend.data;

import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.data.mapper.ItemPricingMapper;
import com.example.hypixeltrackerbackend.data.mapper.StaticItemMapper;
import com.example.hypixeltrackerbackend.services.ApiFetcherService;
import com.example.hypixeltrackerbackend.services.exceptions.HTTPRequestException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseIntegrityCheckIT {
    @Autowired
    private ApiFetcherService apiFetcherService;
    //This is the list of all old id that are still
    private final List<String> legacyItemList = List.of("AMALGAMATED_CRIMSONITE","BAZAAR_COOKIE","ENCHANTED_HAY_BLOCK","TIGHTLY_TIED_HAY_BALE","ENCHANTED_CARROT_ON_A_STICK");

    @Test
    void shouldDatabaseBeUpToDate() throws IOException, HTTPRequestException {
        Map<String, CompleteItem> items = StaticItemMapper.generate();
        Map<String, ItemPricing> bazaarItemList = ItemPricingMapper.toBazaarItems(apiFetcherService.getBazaar());
        SoftAssertions assertions = new SoftAssertions();
        bazaarItemList.keySet().forEach(key -> assertions.assertThat(key).satisfiesAnyOf(
                k -> assertThat(items.get(k)).as("check if %s exist in local database", k).isNotNull(),
                k -> assertThat(k).as("check if %s is an enchantment", k).startsWith("ENCHANTMENT_"),
                k -> assertThat(legacyItemList).as("check if %s is a legacy item", k).contains(k)
        ));
        assertions.assertAll();
    }
}
