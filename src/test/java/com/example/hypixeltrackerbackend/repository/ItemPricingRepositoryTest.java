package com.example.hypixeltrackerbackend.repository;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.byLessThan;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemPricingRepositoryTest {
    @Autowired
    private ItemPricingRepository repository;

    private static final String TEST_STRING = "test";

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldGroupRequestWorkProperlyWithOneItem() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime));
        repository.save(new ItemPricing(TEST_STRING, 8d, 10d, testTime.minusMinutes(2)));

        List<ItemPricing> grouping = repository.groupAllByTimestampBetween(testTime.minusMinutes(3), testTime);
        assertThat(grouping).filteredOn(e->TEST_STRING.equals(e.getItemId())).singleElement().satisfies(price -> {
            AssertionsForClassTypes.assertThat(price.getItemId()).isEqualTo(TEST_STRING);
            AssertionsForClassTypes.assertThat(price.getBuyPrice()).isEqualTo(11);
            AssertionsForClassTypes.assertThat(price.getSellPrice()).isEqualTo(9);
            AssertionsForClassTypes.assertThat(price.getLastUpdate()).isCloseTo(testTime.minusMinutes(3), byLessThan(1, ChronoUnit.SECONDS));
        });
    }

    @Test
    void shouldGroupRequestUseBeginningOfTimeWindow() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime));
        repository.save(new ItemPricing(TEST_STRING, 8d, 10d, testTime.minusMinutes(2)));

        List<ItemPricing> grouping = repository.groupAllByTimestampBetween(testTime.minusMinutes(3), testTime);
        assertThat(grouping).filteredOn(e->TEST_STRING.equals(e.getItemId())).singleElement().satisfies(price -> AssertionsForClassTypes.assertThat(price.getLastUpdate()).isEqualTo(testTime.minusMinutes(3)));

    }
}
