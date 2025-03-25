package com.example.hypixeltrackerbackend.utils.request_parsers;

import com.example.hypixeltrackerbackend.data.responses.UUIDResponse;
import com.example.hypixeltrackerbackend.services.exceptions.HTTPRequestException;
import org.json.JSONObject;

import java.net.http.HttpResponse;

public class UUIDRequestParser extends AbstractRequestParser {
    private static final String DATA_FIELD = "data";

    private UUIDRequestParser() {
    }

    /**
     * Parse a response from playerDB to extract UUID
     *
     * @param responseBody raw response body
     * @return the player full uuid
     * @throws HTTPRequestException if a bad request answer is processed
     */
    public static UUIDResponse parse(HttpResponse<String> responseBody) throws HTTPRequestException {
        checkResponseValidity(responseBody);
        JSONObject answerBody = new JSONObject(responseBody.body());
        JSONObject player = answerBody.getJSONObject(DATA_FIELD).getJSONObject("player");
        return new UUIDResponse(player.getString("id"), player.getString("username"), player.getString("avatar"));
    }
}
