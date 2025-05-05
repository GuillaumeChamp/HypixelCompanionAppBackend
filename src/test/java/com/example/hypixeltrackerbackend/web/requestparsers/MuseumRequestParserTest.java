package com.example.hypixeltrackerbackend.web.requestparsers;

import com.example.hypixeltrackerbackend.testutils.RequestTestUtil;
import com.example.hypixeltrackerbackend.web.exceptions.HTTPRequestException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MuseumRequestParserTest {

    @Test
    void shouldExtractProfilesNamesWorkProperlyWithAWellFormedResponse() throws IOException, HTTPRequestException {
        String payload = Files.readString(Path.of("src/test/resources/museumByProfile.json"));
        HttpResponse<String> response = RequestTestUtil.createMockHttpResponse(200, payload);
        // When
        Set<String> itemsInMuseum = MuseumRequestParser.parse(response);
        // Then
        assertThat(itemsInMuseum).hasSize(270);
    }
}