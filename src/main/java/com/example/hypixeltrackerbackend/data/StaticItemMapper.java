package com.example.hypixeltrackerbackend.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaticItemMapper {
    private static final Logger logger = Logger.getLogger(StaticItemMapper.class.getName());

    private StaticItemMapper(){
    }

    /**
     * Generate the list of all tracked items.
     * Notice that a current pricing part is empty at this moment
     * @return a map of the items using the id as a key
     * @throws IOException if an error occurred while reading the local database
     */
    public static Map<String,CompleteItem> generate() throws IOException {

        try (InputStream stream1 = StaticItemMapper.class.getResourceAsStream("/static/categoryTable.json");
             InputStream stream2 = StaticItemMapper.class.getResourceAsStream("/static/items.json")
        ) {

            assert stream1 != null;
            String myFile = new String(stream1.readAllBytes());
            assert stream2 != null;
            String itemFile = new String(stream2.readAllBytes());

            JSONArray myArray = new JSONObject(myFile).getJSONArray("myTable");
            JSONArray itemArray = new JSONObject(itemFile).getJSONArray("items");

            Map<String,CompleteItem> completeItemMap = new HashMap<>(myArray.length());

            myArray.forEach(object -> appendANewEntry(completeItemMap,(JSONObject) object));
            itemArray.forEach(object -> updateEntry(completeItemMap,(JSONObject) object));

            return completeItemMap;
        }catch (IOException io){
            logger.log(Level.SEVERE,"ERROR WHILE READING LOCAL DATABASE FILE");
            throw io;
        }
    }

    private static void appendANewEntry(Map<String, CompleteItem> completeItemMap, JSONObject object) {
        final String field_category = "category";
        final String field_tag = "tag";
        final String field_image= "image";

        String id = object.getString("id");
        if (!object.has(field_category)){
            String message = createErrorMessage(id,field_category);
            logger.log(Level.WARNING, message);
            return;
        }
        String category = object.getString(field_category);
        if (!object.has(field_tag)){
            String message = createErrorMessage(id,field_tag);
            logger.log(Level.WARNING, message);
        }
        String tag = object.getString(field_tag);
        if (!object.has(field_image)){
            String message = createErrorMessage(id,field_image);
            logger.log(Level.WARNING, message);
        }
        String image = object.getString(field_image);

        CompleteItem completeItem = new CompleteItem(id,category,tag,image);
        handleMissingName(completeItem,id);

        if (object.has("craft")){
            JSONArray craftArray = object.getJSONArray("craft");
            List<Craft> craftList = new ArrayList<>(craftArray.length());
            for(Object jsonObject : craftArray){
                JSONObject craft = (JSONObject) jsonObject;
                List<String> materials = craft.getJSONArray("material").toList().stream().map(String.class::cast).toList();
                List<Float> quantities = craft.getJSONArray("quantity").toList().stream().map(StaticItemMapper::convertToFloat).toList();
                craftList.add(new Craft(materials,quantities));
            }
            completeItem.setCrafts(craftList);
        }

        completeItemMap.put(id ,completeItem);
    }

    private static void handleMissingName(CompleteItem completeItem, String id) {
        if (id.contains("ESSENCE_")){
            completeItemForEssence(completeItem,id);
        } else if (id.equals("SLEEPY_HOLLOW")) {
            completeItem.setName("Sleepy Hollow");
            completeItem.setTier("EPIC");
        }
    }

    private static void completeItemForEssence(CompleteItem completeItem,String id) {
        completeItem.setName(id.replace("ESSENCE_","Essence of "));
        completeItem.setTier("UNCOMMON");
    }

    /**
     * Use the given object to complete the relative item in the map
     * Remarque : even if the map is way shorter than the item database, it is still better to work this way.
     * @param map the map of item from local database already parsed
     * @param object the current process json entry
     */
    private static void updateEntry(Map<String,CompleteItem> map, JSONObject object){
        String id = object.getString("id");
        CompleteItem matchingEntry = map.get(id);
        if (matchingEntry == null) {
            return;
        }
        matchingEntry.setName(object.getString("name"));
        matchingEntry.setNpcBuyPrice(object.optFloat("npc_sell_price"));
        String rarity = object.has("tier") ? object.getString("tier") : "COMMON";
        matchingEntry.setTier(rarity);
    }

    private static String createErrorMessage(String id,String field){
        final String errorMessageBeginning = "Missing entry : ";
        return errorMessageBeginning + id + field;
    }

    private static float convertToFloat(Object object){
        if(object instanceof BigDecimal bigDecimal)
            return bigDecimal.floatValue();
        return ((Integer) object).floatValue();
    }
}
