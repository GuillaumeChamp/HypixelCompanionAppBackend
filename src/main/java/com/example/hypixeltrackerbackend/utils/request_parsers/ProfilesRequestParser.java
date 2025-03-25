package com.example.hypixeltrackerbackend.utils.request_parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfilesRequestParser extends AbstractRequestParser {
    private ProfilesRequestParser() {}

    public static Map<String,String> extractProfilesNames(String profilesPayload){
        JSONObject profilesJSON = new JSONObject(profilesPayload);
        JSONArray profilesArray = profilesJSON.getJSONArray("profiles");
        Map<String,String> profilesNames = new HashMap<>();
        profilesArray.forEach(item -> {
            JSONObject profile = (JSONObject) item;
            profilesNames.put(profile.getString("cute_name"),profile.getString("profile_id"));
        });
        return profilesNames;
    }
}
