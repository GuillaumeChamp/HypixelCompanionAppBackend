package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@SpringBootTest
class SchedulerServiceTests {
    @Autowired
    SchedulerService schedulerService;
    @Autowired
    ApiFetcherService apiFetcherService;

    @Test
    void shouldSchedulerStartAutomatically(){
        assertThat(schedulerService.isStarted()).isTrue();
    }

    @Test
    void assertDelay() {
        LocalDateTime before = apiFetcherService.getLastBazaarAnswer();
        if (before == null) {
            Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND+1,TimeUnit.SECONDS)
                    .until(()-> apiFetcherService.getLastBazaarAnswer()!=null);
            before = apiFetcherService.getLastBazaarAnswer();
        }
        LocalDateTime finalBefore = before;
        Awaitility.waitAtMost(TimeConstant.CALL_FREQUENCY_IN_SECOND*2, TimeUnit.SECONDS)
                .until(() -> !finalBefore.toLocalTime().equals(apiFetcherService.getLastBazaarAnswer().toLocalTime()));
    }

    @Test
    void shouldDoubleStartBeIgnored(){
        assertThatCode(() -> schedulerService.start()).doesNotThrowAnyException();
        assertThatCode(() -> schedulerService.start()).doesNotThrowAnyException();
    }

    @Test
    void shouldStopCommandWorkProperly(){
        assertThatCode(() -> schedulerService.stop()).doesNotThrowAnyException();
        assertThat(schedulerService.isStarted()).isFalse();
        assertThatCode(() -> schedulerService.start()).doesNotThrowAnyException();
        assertThat(schedulerService.isStarted()).isTrue();
    }
}
