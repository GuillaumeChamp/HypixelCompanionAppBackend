package com.example.hypixeltrackerbackend.services;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataFetcher {
    private static final String API_ENDPOINT = "https://api.hypixel.net/v2/skyblock/bazaar";
    private static final Logger logger = Logger.getLogger(DataFetcher.class.getName());
    private static LocalDateTime lastAnswer = null;

    private DataFetcher() {
    }

    /**
     * Make a request for current pricing
     * @return a string with the content of the request
     */
    public static String queryBazaarData() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_ENDPOINT))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                logger.log(Level.INFO, ()->"successfully recovered bazaar data from Hypixel.");
                DataFetcher.lastAnswer = LocalDateTime.now();
                return response.body();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING,()->"No response received \n" + Arrays.toString(e.getStackTrace()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public static LocalDateTime getLastAnswer() {
        return DataFetcher.lastAnswer;
    }
}
