package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.repository.ItemPricingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DataProcessorServiceTest {
    @Autowired
    private DataProcessorService dataProcessorService;
    @Autowired
    private ItemPricingRepository repository;

    private static final String TEST_STRING = "test";

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldGroupingLastHourWorkWithFewItems() {
        final String testItemId1 = "test1";
        final String testItemId2 = "test2";

        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemPricing itemPricing = new ItemPricing(testItemId1, 10d, 12d, testTime);
        ItemPricing itemPricing1 = new ItemPricing(testItemId1, 8d, 10d, testTime.minusMinutes(2));

        ItemPricing itemPricing3 = new ItemPricing(testItemId2, 4d, 8d, testTime);
        ItemPricing itemPricing4 = new ItemPricing(testItemId2, 2d, 6d, testTime.minusMinutes(2));

        repository.save(itemPricing);
        repository.save(itemPricing1);

        repository.save(itemPricing3);
        repository.save(itemPricing4);

        dataProcessorService.groupOneHourRecords(testTime.minusHours(1));
        LocalDateTime compressedUpdateTime = testTime.minusMinutes(TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
        List<ItemPricing> pricing1 = repository.findAllByItemId(testItemId1);
        assertThat(pricing1).singleElement().satisfies(e->{
            assertThat(e.getLastUpdate()).isEqualTo(compressedUpdateTime);
            assertThat(e.getSellPrice()).isEqualTo(9);
            assertThat(e.getBuyPrice()).isEqualTo(11);
        });

        List<ItemPricing> pricing2 = repository.findAllByItemId(testItemId2);
        assertThat(pricing2).singleElement().satisfies(e -> {
                    assertThat(e.getLastUpdate()).isEqualTo(compressedUpdateTime);
                    assertThat(e.getSellPrice()).isEqualTo(3);
                    assertThat(e.getBuyPrice()).isEqualTo(7);
                });
    }

    @Test
    void shouldGroupingLastHourSampleCorrectly() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime));
        repository.save(new ItemPricing(TEST_STRING, 8d, 10d, testTime.minusMinutes(2)));

        LocalDateTime secondTimeStamp = testTime.minusMinutes(TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
        repository.save(new ItemPricing(TEST_STRING, 20d, 40d, secondTimeStamp));
        repository.save(new ItemPricing(TEST_STRING, 16d, 30d, secondTimeStamp.minusMinutes(2)));

        dataProcessorService.groupOneHourRecords(testTime.minusHours(1));

        List<ItemPricing> pricing = repository.findAllByItemId(TEST_STRING);
        assertThat(pricing).hasSize(2).anySatisfy(e -> {
            assertThat(e.getSellPrice()).isEqualTo(9);
            assertThat(e.getBuyPrice()).isEqualTo(11);
            final LocalDateTime expectedTimestamp = testTime.minusMinutes(TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
            assertThat(e.getLastUpdate().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(expectedTimestamp);
        }).anySatisfy(e -> {
            assertThat(e.getSellPrice()).isEqualTo(18);
            assertThat(e.getBuyPrice()).isEqualTo(35);
            final LocalDateTime expectedTimestamp = secondTimeStamp.minusMinutes(TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
            assertThat(e.getLastUpdate().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(expectedTimestamp);
        });
    }

    @Test
    void shouldDoubleGroupingHaveNoEffect() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        repository.save(new ItemPricing(TEST_STRING, 10d, 12d, testTime));
        repository.save(new ItemPricing(TEST_STRING, 8d, 10d, testTime.minusMinutes(2)));

        LocalDateTime secondTimeStamp = testTime.minusMinutes(TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
        repository.save(new ItemPricing(TEST_STRING, 20d, 40d, secondTimeStamp));
        repository.save(new ItemPricing(TEST_STRING, 16d, 30d, secondTimeStamp.minusMinutes(2)));

        dataProcessorService.groupOneHourRecords(testTime.minusHours(1));
        dataProcessorService.groupOneHourRecords(testTime.minusHours(1));

        List<ItemPricing> pricing = repository.findAllByItemId(TEST_STRING);
        assertThat(pricing).hasSize(2);
    }
}
