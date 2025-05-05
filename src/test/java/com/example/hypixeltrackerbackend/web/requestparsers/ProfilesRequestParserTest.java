package com.example.hypixeltrackerbackend.web.requestparsers;

import com.example.hypixeltrackerbackend.testutils.RequestTestUtil;
import com.example.hypixeltrackerbackend.web.exceptions.HTTPRequestException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ProfilesRequestParserTest {

    @Test
    void shouldExtractProfilesNamesWorkProperlyWithAWellFormedResponse() throws IOException, HTTPRequestException {
        String payload = Files.readString(Path.of("src/test/resources/profilesByPlayer.json"));
        HttpResponse<String> response = RequestTestUtil.createMockHttpResponse(200, payload);
        // When
        Map<String, String> profileMap = ProfilesRequestParser.parse(response);
        // Then
        assertThat(profileMap).hasSize(2)
                .containsEntry("Kiwi","d98de6ca-7b9b-463e-a29c-4bfd183349d0")
                .containsEntry("Lime","acf783a1-938e-4dad-9f47-79de29bdc16f");
    }
}