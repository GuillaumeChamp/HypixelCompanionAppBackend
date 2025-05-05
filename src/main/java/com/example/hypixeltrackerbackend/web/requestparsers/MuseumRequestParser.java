package com.example.hypixeltrackerbackend.web.requestparsers;

import com.example.hypixeltrackerbackend.utils.CollectionsUtils;
import com.example.hypixeltrackerbackend.web.exceptions.HTTPRequestException;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;

public class MuseumRequestParser extends AbstractRequestParser {

    private MuseumRequestParser() {
    }

    public static Set<String> parse(HttpResponse<String> responseBody) throws HTTPRequestException {
        checkResponseValidity(responseBody);
        JSONObject jsonObject = new JSONObject(responseBody.body());
        return extractItemsIds(jsonObject);
    }

    @SuppressWarnings("unchecked")
    private static Set<String> extractItemsIds(JSONObject jsonObject) {
        Map<String, Object> museumDataForTheProfile = (Map<String, Object>) CollectionsUtils.extractOnlyObjectInCollection(jsonObject.getJSONObject("members").toMap().values());
        Map<String ,String > itemsMap = (Map<String ,String >) museumDataForTheProfile.get("items");
        return itemsMap.keySet();
    }
}
