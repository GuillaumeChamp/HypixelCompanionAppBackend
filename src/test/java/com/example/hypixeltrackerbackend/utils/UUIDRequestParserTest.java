package com.example.hypixeltrackerbackend.utils;

import com.example.hypixeltrackerbackend.services.exceptions.HTTPRequestException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UUIDRequestParserTest {
    private static final String AYPIERRE_ID = "6c4ea7ff-b834-408c-9368-fbd2a164ee0c";
    private static final String PLAYER_NOT_FOUND_PAYLOAD = "{\"message\":\"No Minecraft user could be found.\",\"code\":\"minecraft.invalid_username\",\"data\":{},\"success\":false,\"error\":false}";

    @Test
    void shouldParseSampleRequest() throws IOException, HTTPRequestException {
        String payload = Files.readString(Path.of("src/test/resources/playerdb_sample_request.json"));
        HttpResponse<String> response = createMockHttpResponse(500, payload);
        assertThat(UUIDRequestParser.parse(response).uuid()).isEqualTo(AYPIERRE_ID);
    }

    @Test
    void shouldThrowExceptionWhileParsingNullResponse() {
        assertThatThrownBy(() -> UUIDRequestParser.parse(null)).isInstanceOf(HTTPRequestException.class);
    }

    @Test
    void shouldThrowExceptionWhileParsingPlayerNotFound() {
        HttpResponse<String> response = createMockHttpResponse(200, PLAYER_NOT_FOUND_PAYLOAD);
        assertThatThrownBy(() -> UUIDRequestParser.parse(response)).isInstanceOf(HTTPRequestException.class).hasMessageContaining("not found");
    }

    @Test
    void shouldThrowExceptionWhileParsingWithMissingKey() {
        HttpResponse<String> response = createMockHttpResponse(404, "{\"success\":true}");
        assertThatThrownBy(() -> UUIDRequestParser.parse(response)).isInstanceOf(JSONException.class).hasMessageContaining("not found");
    }

    HttpResponse<String> createMockHttpResponse(int statusCode, String payload) {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return statusCode;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return payload;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }
}