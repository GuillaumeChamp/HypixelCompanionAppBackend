package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.repository.ItemPricingRepository;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class SchedulerServiceTests {
    @Autowired
    SchedulerService schedulerService;
    @Autowired
    ApiFetcherService apiFetcherService;
    @Autowired
    ItemPricingRepository itemPricingRepository;

    @Test
    void shouldSchedulerStartAutomatically() {
        assertThat(schedulerService.isStarted()).isTrue();
    }

    @Test
    void shouldLastYearRecordBeDeletedAtStartup() throws IOException {
        String testString = "scheduler_startup_test";
        schedulerService.stop();
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        itemPricingRepository.save(new ItemPricing(testString, 8d, 10d, testTime.minusYears(1).minusDays(3)));
        itemPricingRepository.save(new ItemPricing(testString, 10d, 12d, testTime.minusDays(3)));

        schedulerService.start();
        Awaitility.await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
        AssertionsForInterfaceTypes.assertThat(itemPricingRepository.findAllByItemId(testString))
                .singleElement()
                .extracting(ItemPricing::getTime)
                .asInstanceOf(InstanceOfAssertFactories.LOCAL_DATE_TIME)
                .isAfterOrEqualTo(testTime.minusYears(1));
    }

    @Test
    void shouldCallFrequencyBeAccurate() {
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND + 1, TimeUnit.SECONDS)
                .until(() -> apiFetcherService.getLastBazaarAnswer() != null);
        LocalDateTime before = apiFetcherService.getLastBazaarAnswer();

        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND * 2, TimeUnit.SECONDS)
                .until(() -> !before.equals(apiFetcherService.getLastBazaarAnswer()));
    }

    @Test
    void shouldDoubleStartBeIgnored() throws IOException {
        schedulerService.start();
        schedulerService.start();

        assertThat(schedulerService.isStarted()).isTrue();
    }

    @Test
    void shouldStopCommandWorkProperly() throws IOException {
        schedulerService.stop();
        assertThat(schedulerService.isStarted()).isFalse();
        schedulerService.start();
        assertThat(schedulerService.isStarted()).isTrue();
    }
}
