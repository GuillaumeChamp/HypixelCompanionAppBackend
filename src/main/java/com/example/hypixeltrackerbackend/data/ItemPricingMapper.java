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
            itemList.put(newItem.getItemId(), newItem);
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
        double sellPrice;
        Double buyPrice = null;

        JSONArray sellSummary = entry.getJSONArray("sell_summary");
        if (!sellSummary.isEmpty()) {
            JSONObject highestSellOrder = sellSummary.getJSONObject(0);
            sellPrice = highestSellOrder.getDouble(PRICE_PER_UNIT);
        }else {
            sellPrice = 0.1;
        }

        JSONArray buySummary = entry.getJSONArray("buy_summary");
        if (!buySummary.isEmpty()) {
            JSONObject lowestBuyOrder = buySummary.getJSONObject(0);
            buyPrice = lowestBuyOrder.getDouble(PRICE_PER_UNIT);
        }

        return new ItemPricing(entry.getString("product_id"), sellPrice, buyPrice,update);
    }
}

