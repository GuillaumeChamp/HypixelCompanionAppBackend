package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.data.responses.UUIDResponse;
import com.example.hypixeltrackerbackend.utils.UUIDRequestParser;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HypixelApiCaller {
    private static final String API_ENDPOINT = "https://api.hypixel.net/v2/skyblock/";
    private static final String PLAYER_DB_ENDPOINT = "https://playerdb.co/api/player/minecraft/";
    private static final Logger logger = Logger.getLogger(HypixelApiCaller.class.getName());
    private static LocalDateTime lastBazaarAnswer = null;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String API_KEY = System.getenv("HYPIXEL_API_KEY");

    private HypixelApiCaller() {
    }

    public static LocalDateTime getLastBazaarAnswer() {
        return HypixelApiCaller.lastBazaarAnswer;
    }

    /**
     * Make a request for current pricing
     *
     * @return a string with the content of the request, null if an error occurred and log the error
     */
    public static String getBazaar() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_ENDPOINT.concat("bazaar")))
                .build();
        try {
            HttpResponse<String> response = sendRequest(request);
            logger.log(Level.INFO, () -> "successfully recovered bazaar data.");
            HypixelApiCaller.lastBazaarAnswer = LocalDateTime.now();
            return response.body();
        } catch (HTTPRequestException e) {
            logger.log(Level.WARNING, () -> "No response received");
            return null;
        }
    }

    /**
     * Make a request to the Hypixel Api to ask profiles data of a player
     *
     * @param uuid Player UUID
     * @return an unparsed response body
     * @throws HTTPRequestException if a bad response is received
     */
    public static String getProfileByUUID(String uuid) throws HTTPRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_ENDPOINT + "profiles" + "?key=" + API_KEY + "&uuid=" + uuid))
                .build();

        HttpResponse<String> response = sendRequest(request);
        return response.body();
    }

    /**
     * Make a request to playerDB to resolve the uuid of a player
     *
     * @param username the player username
     * @return a full player uuid
     * @throws HTTPRequestException if player is not found or an error occurred with the http response
     */
    public static UUIDResponse getUUIDFromUsername(String username) throws HTTPRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(PLAYER_DB_ENDPOINT + username))
                .build();
        HttpResponse<String> response = sendRequest(request);
        return UUIDRequestParser.parse(response);
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws HTTPRequestException {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new HTTPRequestException("bad request " + response.statusCode());
            }
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalThreadStateException("request interrupted");
        } catch (IOException e) {
            throw new HTTPRequestException("No response received");
        }
    }

}
