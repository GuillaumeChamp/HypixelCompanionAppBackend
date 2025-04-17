package com.example.hypixeltrackerbackend.utils;

import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import com.example.hypixeltrackerbackend.data.bazaar.Craft;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.services.exceptions.DataProcessingException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemPricingUtil {
    private static final Logger logger = Logger.getLogger(ItemPricingUtil.class.getName());
    private static Map<String, CompleteItem> completeItemHashMap;

    private ItemPricingUtil() {
    }

    public static void updateAllItemsMinimalCost(Map<String, CompleteItem> completeItemHashMap) {
        ItemPricingUtil.completeItemHashMap = completeItemHashMap;
        completeItemHashMap.forEach((key, value) -> ItemPricingUtil.computeItemMinimalCost(value));
    }

    /**
     * Resolve minimal cost value using craft if available or else buy price.
     * NB if an item have no craft and no bazaar order, the minimal cost will remain null.
     *
     * @param item a completeItem to resolve, will be call recursively if needed
     */
    private static void computeItemMinimalCost(CompleteItem item) {
        if (item.getName() == null) {
            logger.log(Level.WARNING, "item without name : {0} please check if item database is up to date", item);
            return;
        }
        if (CollectionsUtils.isEmpty(item.getCrafts())) {
            if (item.getPricing() == null) {
                logger.fine("no pricing for " + item.getName());
                return;
            }
            if (item.getPricing().getBuyPrice() == null) {
                logger.fine("no buy price" + item.getName() + " might not have sell order at this moment");
                return;
            }
            item.getPricing().setMinimalPrice(item.getPricing().getBuyPrice());
            return;
        }
        for (Craft craft : item.getCrafts()) {
            try {
                updateMinimalCostUsingOneCraft(item, craft);
            } catch (DataProcessingException e) {
                logger.log(Level.INFO, e::getMessage);
            }
        }
    }

    /**
     * Update the minimal cost by resolving the crafting cost using the given craft
     *
     * @param item  the item to update
     * @param craft one craft of the item to check
     * @throws DataProcessingException if unable to compute craft cost
     */
    private static void updateMinimalCostUsingOneCraft(CompleteItem item, Craft craft) throws DataProcessingException {
        if (craft.getCraftingCost() == null) {
            computeCraftCost(craft);
        }
        // check pricing after because craft cost can be useful even if there is no pricing
        if (item.getPricing() == null) {
            logger.fine(item.getName() + " : has no pricing");
            return;
        }
        updateMinimalPrice(item.getPricing(), craft.getCraftingCost());
    }

    /**
     * Update the minimal price value by setting it to the default (i.e. the buy cost)
     *
     * @param pricing      the pricing sheet of an item
     * @param craftingCost the cost to check with
     */
    private static void updateMinimalPrice(ItemPricing pricing, Double craftingCost) {
        Double buyPrice = pricing.getBuyPrice();
        // by the provider api, the buyPrice is 0 if item is not currently sell
        if (buyPrice == null || buyPrice > craftingCost) {
            pricing.setMinimalPrice(craftingCost);
        } else {
            pricing.setMinimalPrice(buyPrice);
        }
    }

    /**
     * Compute the crafting cost of an item
     *
     * @param craft the craft to complete
     * @throws DataProcessingException is thrown if it cannot compute cost of a material
     */
    private static void computeCraftCost(Craft craft) throws DataProcessingException {
        double craftingCost = 0;
        List<String> materials = craft.getMaterialIdList();
        List<Float> quantities = craft.getQuantityOfMaterial();
        for (int i = 0; i < materials.size(); i++) {
            String materialId = materials.get(i);
            float quantity = quantities.get(i);
            Double extractedCost = extractCostForAMaterial(materialId);
            if (extractedCost == null) {
                throw new DataProcessingException("Cannot extract cost of : " + materialId, craft);
            }
            craftingCost += extractedCost * quantity;
        }
        craft.setCraftingCost(craftingCost);
    }

    /**
     * Extract the cost of a material
     *
     * @param materialId id of a material to find
     * @return the extracted value
     * @throws DataProcessingException is thrown when a material is not found or if an item have no minimal price even after a call to compute item minimal price
     */
    private static Double extractCostForAMaterial(String materialId) throws DataProcessingException {
        if (!ItemPricingUtil.completeItemHashMap.containsKey(materialId)) {
            throw new DataProcessingException(materialId.concat(" : is missing from local database"));
        }
        CompleteItem completeItem = ItemPricingUtil.completeItemHashMap.get(materialId);
        ItemPricing itemPricing = completeItem.getPricing();
        double minimalPrice;
        if (itemPricing == null) {
            minimalPrice = handleMissingPricing(completeItem);
        } else {
            if (itemPricing.getMinimalPrice() == null) {
                computeItemMinimalCost(completeItem);
            }
            if (itemPricing.getMinimalPrice() == null) {
                throw new DataProcessingException("unable to compute minimal cost", completeItem);
            }
            minimalPrice = itemPricing.getMinimalPrice();
        }
        return minimalPrice;
    }

    private static double handleMissingPricing(CompleteItem completeItem) throws DataProcessingException {
        if (CollectionsUtils.isNotEmpty(completeItem.getCrafts())) {
            if (completeItem.getCrafts().getFirst().getCraftingCost() == null) {
                // old school for loop to handle exception
                for (Craft craft : completeItem.getCrafts()) {
                    computeCraftCost(craft);
                }
            }
            return completeItem.getCrafts().getFirst().getCraftingCost();
        } else {
            throw new DataProcessingException("no pricing and no craft, craft aborted", completeItem);
        }
    }
}
