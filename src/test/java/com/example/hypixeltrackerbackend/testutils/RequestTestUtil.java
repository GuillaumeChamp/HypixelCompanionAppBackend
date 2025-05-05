package com.example.hypixeltrackerbackend.testutils;

import org.mockito.Mockito;

import java.net.http.HttpResponse;

import static org.mockito.Mockito.when;

public class RequestTestUtil {

    @SuppressWarnings("unchecked")
    public static HttpResponse<String> createMockHttpResponse(int statusCode, String payload) {
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        when(response.body()).thenReturn(payload);
        when(response.statusCode()).thenReturn(statusCode);
        return response;
    }
}
