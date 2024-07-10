package com.example.hypixeltrackerbackend.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class ItemPricingMapper {
    private static final String PRICE_PER_UNIT = "pricePerUnit";

    private ItemPricingMapper() {
    }

    public static Map<String , ItemPricing> toBazaarItems(String string) {
        JSONObject response = new JSONObject(string);
        LocalDateTime update = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        JSONObject productList = response.getJSONObject("products");
        JSONArray listOfEntry = productList.toJSONArray(productList.names());

        HashMap<String, ItemPricing> itemList = new HashMap<>(1500);

        listOfEntry.forEach(object -> {
            ItemPricing newItem = readAnEntry((JSONObject) object,update);
            if (newItem != null) {
                itemList.put(newItem.getItemId(),newItem);
            }
        });

        return itemList;
    }

    /**
     * Create a new Bazaar Item reading an entry
     * @param entry a Json object matching an item
     * @param update the request timestamp
     * @return teh parsed Bazaar Item
     */
    private static ItemPricing readAnEntry(JSONObject entry, LocalDateTime update) {
        JSONArray sellSummary = entry.getJSONArray("sell_summary");
        if (sellSummary.isEmpty()) {
            return null;
        }
        JSONObject lastSellOrder = sellSummary.getJSONObject(sellSummary.length() - 1);
        Double sellPrice = lastSellOrder.getDouble(PRICE_PER_UNIT);
        //by design of the providing api, 0 means that the item is not currently sold
        if (sellPrice==0){
            sellPrice=null;
        }

        JSONArray buySummary = entry.getJSONArray("buy_summary");
        if (buySummary.isEmpty()) {
            return null;
        }
        JSONObject firstBuyOrder = buySummary.getJSONObject(0);
        Double buyPrice = firstBuyOrder.getDouble(PRICE_PER_UNIT);
        // same comment as before
        if (buyPrice==0){
            buyPrice=null;
        }

        return new ItemPricing(entry.getString("product_id"), sellPrice, buyPrice,update);
    }
}

