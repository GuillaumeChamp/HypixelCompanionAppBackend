package com.example.hypixeltrackerbackend.web.requestparsers;

import com.example.hypixeltrackerbackend.web.exceptions.HTTPRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ProfilesRequestParser extends AbstractRequestParser {

    private ProfilesRequestParser() {
    }

    public static Map<String, String> parse(HttpResponse<String> responseBody) throws HTTPRequestException {
        checkResponseValidity(responseBody);
        JSONObject profilesJSON = new JSONObject(responseBody.body());
        JSONArray profilesArray = profilesJSON.getJSONArray("profiles");
        Map<String, String> profilesNames = new HashMap<>();
        profilesArray.forEach(item -> {
            JSONObject profile = (JSONObject) item;
            profilesNames.put(profile.getString("cute_name"), profile.getString("profile_id"));
        });
        return profilesNames;
    }

}
