package com.example.hypixeltrackerbackend.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility to generate the list of Museum items
 */
public class MuseumItemMapper {
    private static final Logger logger = Logger.getLogger(MuseumItemMapper.class.getName());
    private static final String ARMOR_SET_DONATION_XP = "armor_set_donation_xp";
    private static final String PROCESSED = "processed";
    private static final String MUSEUM_DATA = "museum_data";

    private MuseumItemMapper() {
    }

    public static List<MuseumItem> generateMuseumItemList() throws IOException {
        try (InputStream items = MuseumItemMapper.class.getResourceAsStream("/static/items.json")) {
            assert items != null;
            String itemListPayload = new String(items.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray itemsAsJSONArray = new JSONObject(itemListPayload).getJSONArray("items");

            Map<String, JSONObject> museumItemMap = new HashMap<>();
            itemsAsJSONArray.forEach(item -> addMuseumItem(museumItemMap, (JSONObject) item));
            groupArmorSet(museumItemMap);
            return museumItemMap.entrySet().stream().map(entry -> new MuseumItem(entry.getKey(), entry.getValue())).toList();
        } catch (IOException io) {
            logger.warning("Error while loading Museum Items");
            throw io;
        }
    }

    private static void addMuseumItem(Map<String, JSONObject> museumItemMap, JSONObject item) {
        if (item.has(MUSEUM_DATA)) {
            museumItemMap.put(item.getString("id"), item.getJSONObject(MUSEUM_DATA).put("name",item.getString("name")));
        }
    }

    private static void groupArmorSet(Map<String, JSONObject> museumItemMap) {
        Map<String, JSONObject> armorPieces = extractListOfArmorPieces(museumItemMap);
        Map<String, JSONObject> fullArmorSet = new HashMap<>();
        for (Map.Entry<String, JSONObject> entry : armorPieces.entrySet()) {
            if (entry.getValue().has(PROCESSED)) {
                continue;
            }
            String setName = entry.getValue().getJSONObject(ARMOR_SET_DONATION_XP).keySet().stream().findFirst().orElseThrow();
            Map<String, JSONObject> setMember = extractListOfArmorPiecesWithSetName(armorPieces, setName);
            JSONObject parsedArmorSet = entry.getValue();
            parsedArmorSet.put("name", setName.toLowerCase().replace("_", " ") + " set");
            parsedArmorSet.put("pieces", setMember.keySet());
            parsedArmorSet.put("donation_xp", parsedArmorSet.getJSONObject(ARMOR_SET_DONATION_XP).getInt(setName));
            parsedArmorSet.remove(ARMOR_SET_DONATION_XP);
            fullArmorSet.put(setName, parsedArmorSet);
            setMember.values().forEach(jsonObject -> jsonObject.put(PROCESSED, true));
        }
        museumItemMap.putAll(fullArmorSet);
    }

    private static Map<String, JSONObject> extractListOfArmorPieces(Map<String, JSONObject> museumItemMap) {
        Map<String, JSONObject> armorPieces = new HashMap<>();
        for (Map.Entry<String, JSONObject> entry : museumItemMap.entrySet()) {
            if (entry.getValue().has(ARMOR_SET_DONATION_XP)) {
                armorPieces.put(entry.getKey(), entry.getValue());
            }
        }
        armorPieces.forEach((key, value) -> museumItemMap.remove(key));
        return armorPieces;
    }

    private static Map<String, JSONObject> extractListOfArmorPiecesWithSetName(Map<String, JSONObject> armorPieces, String setName) {
        return armorPieces.entrySet()
                .stream()
                .filter(armorPieceEntry -> checkIfBelongToASet(setName, armorPieceEntry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean checkIfBelongToASet(String setName, JSONObject objetToTest) {
        return !objetToTest.has(PROCESSED)
                && setName.equals(objetToTest.getJSONObject(ARMOR_SET_DONATION_XP).keySet().stream().findFirst().orElseThrow());
    }

}
