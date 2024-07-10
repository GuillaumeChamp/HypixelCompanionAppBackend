package com.example.hypixeltrackerbackend.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DataFetcherTest {

    @Test
    void shouldDataBeRecovered() {
        String payload = DataFetcher.queryBazaarData();
        assertThat(payload)
                .isNotEmpty()
                .startsWith("{")
                .contains("\"success\":true,")
                .contains("\"products\":{")
                .endsWith("}");
    }

    @Test
    void shouldLastAnswerBeUpdated() {
        LocalDateTime before = DataFetcher.getLastAnswer();
        DataFetcher.queryBazaarData();
        LocalDateTime after = DataFetcher.getLastAnswer();
        assertThat(before).isBefore(after);
    }
}
