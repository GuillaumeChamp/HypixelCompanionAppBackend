package com.example.hypixeltrackerbackend.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class HypixelApiCallerTest {

    @Test
    void shouldDataBeRecovered() {
        String payload = HypixelApiCaller.getBazaar();
        assertThat(payload)
                .isNotEmpty()
                .startsWith("{")
                .contains("\"success\":true,")
                .contains("\"products\":{")
                .endsWith("}");
    }

    @Test
    void shouldLastAnswerBeUpdated() {
        LocalDateTime before = HypixelApiCaller.getLastBazaarAnswer();
        HypixelApiCaller.getBazaar();
        LocalDateTime after = HypixelApiCaller.getLastBazaarAnswer();
        assertThat(before).isBefore(after);
    }

    @Disabled("Disabled util received a permanent api key")
    @Test
    void shouldProfileQueryWorkProperly(){
        assertThatNoException().isThrownBy(()->{
            String answer = HypixelApiCaller.getProfileByUUID("75957f87-aaea-4952-953b-6ca217a2654d");
            assertThat(answer).isNotEmpty();
        });
    }

    @Test
    void shouldUUIDRequestWorkProperly(){
        assertThatNoException().isThrownBy(()->{
            String responseCode = HypixelApiCaller.getUUIDFromUsername("TestSubject14840");
            assertThat(responseCode).isEqualTo("75957f87-aaea-4952-953b-6ca217a2654d");
        });
    }

    @Test
    void shouldUUIDRequestFailProperly(){
        assertThatThrownBy(()-> HypixelApiCaller.getUUIDFromUsername("eazehbjkasjlcjxjcbkjsgblkjzaze")).isInstanceOf(HTTPRequestException.class).hasMessageContaining("bad request");
    }
}
