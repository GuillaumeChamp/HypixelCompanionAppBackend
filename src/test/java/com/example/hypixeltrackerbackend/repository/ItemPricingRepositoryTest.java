package com.example.hypixeltrackerbackend.repository;

import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        assertThat(grouping)
                .filteredOn(e -> TEST_STRING.equals(e.getItemId()))
                .singleElement()
                .extracting("itemId", "buyPrice", "sellPrice", "time")
                .containsExactly(TEST_STRING, 11d, 9d, testTime.minusMinutes(3));
    }

    @Test
    void shouldGroupRequestUseBeginningOfTimeWindow() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime));
        repository.save(new ItemPricing(TEST_STRING, 8d, 10d, testTime.minusMinutes(2)));

        List<ItemPricing> grouping = repository.groupAllByTimestampBetween(testTime.minusMinutes(3), testTime);
        assertThat(grouping)
                .filteredOn(e -> TEST_STRING.equals(e.getItemId()))
                .singleElement()
                .satisfies(price -> assertThat(price.getTime()).isEqualTo(testTime.minusMinutes(3)));

    }

    @Test
    @Transactional
    void ShouldDeleteAllByTimeBeforeWorkProperly() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime.minusYears(1).minusDays(3)));
        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime.minusYears(1)));
        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime.minusDays(3)));

        repository.deleteAllByTimeBefore(testTime.minusYears(1));
        assertThat(repository.findAllByItemId(TEST_STRING))
                .hasSize(2)
                .extracting(ItemPricing::getTime)
                .asInstanceOf(InstanceOfAssertFactories.iterable(LocalDateTime.class))
                .allSatisfy(time -> assertThat(time).isAfterOrEqualTo(testTime.minusYears(1)));
    }
}
