package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.data.responses.UUIDResponse;
import com.example.hypixeltrackerbackend.services.exceptions.HTTPRequestException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
class ApiFetcherServiceTest {
    @Autowired
    private ApiFetcherService apiFetcherService;

    @Test
    void shouldDataBeRecovered() throws HTTPRequestException {
        String payload = apiFetcherService.getBazaar();
        assertThat(payload).isNotEmpty().startsWith("{").contains("\"success\":true,").contains("\"products\":{").endsWith("}");
    }

    @Test
    void shouldLastAnswerBeUpdated() throws HTTPRequestException {
        LocalDateTime before = apiFetcherService.getLastBazaarAnswer();
        apiFetcherService.getBazaar();
        LocalDateTime after = apiFetcherService.getLastBazaarAnswer();
        assertThat(before).isBefore(after);
    }

    @Disabled("Disabled util received a permanent api key")
    @Test
    void shouldProfileQueryWorkProperly() {
        assertThatNoException().isThrownBy(() -> {
            String answer = apiFetcherService.getProfileByUUID("75957f87-aaea-4952-953b-6ca217a2654d");
            assertThat(answer).isNotEmpty();
        });
    }

    @Test
    void shouldUUIDRequestWorkProperly() {
        assertThatNoException().isThrownBy(() -> {
            UUIDResponse responseCode = apiFetcherService.getUUIDFromUsername("TestSubject14840");
            assertThat(responseCode.uuid()).isEqualTo("75957f87-aaea-4952-953b-6ca217a2654d");
        });
    }

    @Test
    void shouldUUIDRequestFailProperly() {
        assertThatThrownBy(() -> apiFetcherService.getUUIDFromUsername("eazehbjkasjlcjxjcbkjsgblkjzaze")).isInstanceOf(HTTPRequestException.class).hasMessageContaining("bad request");
    }
}
