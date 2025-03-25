package com.example.hypixeltrackerbackend.utils;

import com.example.hypixeltrackerbackend.utils.request_parsers.ProfilesRequestParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ProfilesRequestParserTest {

    @Test
    void shouldExtractProfilesNamesWorkProperlyWithAWellFormedResponse() throws IOException {
        String payload = Files.readString(Path.of("src/test/resources/profilesByPlayer.json"));
        Map<String, String> profileMap = ProfilesRequestParser.extractProfilesNames(payload);
        assertThat(profileMap).hasSize(2)
                .containsEntry("Kiwi","d98de6ca-7b9b-463e-a29c-4bfd183349d0")
                .containsEntry("Lime","acf783a1-938e-4dad-9f47-79de29bdc16f");
    }
}