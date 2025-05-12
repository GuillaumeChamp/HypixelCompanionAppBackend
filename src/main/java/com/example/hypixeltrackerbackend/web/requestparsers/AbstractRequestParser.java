package com.example.hypixeltrackerbackend.web.requestparsers;

import com.example.hypixeltrackerbackend.web.exceptions.HTTPRequestException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.net.http.HttpResponse;

public abstract class AbstractRequestParser {

    protected AbstractRequestParser() {
    }

    static void checkResponseValidity(HttpResponse<String> responseBody) throws HTTPRequestException {
        if (responseBody == null) {
            throw new HTTPRequestException(HttpStatus.BAD_GATEWAY,"Response body is null");
        }
        JSONObject answerBody = new JSONObject(responseBody.body());
        if (!answerBody.getBoolean("success")) {
            throw new HTTPRequestException(HttpStatus.resolve(responseBody.statusCode()),"Request failed");
        }
    }

}
