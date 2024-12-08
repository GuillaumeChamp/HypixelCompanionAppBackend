package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import com.example.hypixeltrackerbackend.data.*;
import com.example.hypixeltrackerbackend.repository.ItemPricingRepository;
import com.example.hypixeltrackerbackend.utils.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DataProcessorService {
    private static final double MINIMAL_PRICE = 0.1;
    private final ItemPricingRepository pricingRepository;
    private static final Logger logger = Logger.getLogger(DataProcessorService.class.getName());
    private Map<String, CompleteItem> completeItemHashMap;

    @Autowired
    public DataProcessorService(ItemPricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }

    /**
     * Use to load the static part of the data
     *
     * @throws IOException if an error occurred while reading the local database
     */
    public void preloadData() throws IOException {
        if (completeItemHashMap == null) {
            completeItemHashMap = StaticItemMapper.generate();
        }
    }

    /**
     * Get the latest data available
     *
     * @return a hashMap of already processed bazaar's item
     */
    public Map<String, CompleteItem> getLastData() {
        return completeItemHashMap;
    }

    public List<ItemPricing> getHistory(String itemId, String timeWindow) {
        LocalDateTime ending = LocalDateTime.now();
        LocalDateTime beginning = switch (timeWindow) {
            case "day" -> ending.minusHours(24);
            case "hour" -> ending.minusHours(1);
            case "week" -> ending.minusDays(7);
            case "month" -> ending.minusMonths(1);
            case "year" -> ending.minusYears(1);
            default -> LocalDateTime.MIN;
        };
        return pricingRepository.findAllByItemIdAndLastUpdateBetweenOrderByLastUpdate(itemId, beginning, ending);
    }

    /**
     * Group all the records from the given date time and the given date time plus one hour
     * @param beginning the beginning of the compression
     * @see TimeConstant
     */
    @Transactional
    public void groupOneHourRecords(LocalDateTime beginning) {
        LocalDateTime startOfWindow = beginning.truncatedTo(ChronoUnit.SECONDS);

        for (int i = 0; i < TimeConstant.VALUES_BY_HOURS; i++) {
            groupRecordsWithTimeStampAndWindowSize(startOfWindow, TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
            startOfWindow = startOfWindow.plusMinutes(TimeConstant.SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES);
        }
        String logString = "Successfully compress data from " + beginning + " to " + startOfWindow;
        logger.info(logString);
    }

    /**
     * Group all the records from the given date time and the given date time plus one day
     * @param beginning the beginning of the compression
     * @see TimeConstant
     */
    @Transactional
    public void groupOneDayRecords(LocalDateTime beginning) {
        LocalDateTime startOfWindow = beginning.truncatedTo(ChronoUnit.SECONDS);

        for (int i = 0; i < TimeConstant.VALUES_BY_DAYS; i++) {
            groupRecordsWithTimeStampAndWindowSize(startOfWindow, TimeConstant.SAMPLING_BY_DAYS_TIME_SLOT_IN_MINUTES);
            startOfWindow = startOfWindow.plusMinutes(TimeConstant.SAMPLING_BY_DAYS_TIME_SLOT_IN_MINUTES);
        }
        String logString = "Successfully compress data from " + beginning + " to " + startOfWindow;
        logger.log(Level.INFO,logString);
    }

    private void groupRecordsWithTimeStampAndWindowSize(LocalDateTime begin, Integer samplingTimeWindow) {
        List<ItemPricing> summary = pricingRepository.groupAllByTimestampBetween(begin, begin.plusMinutes(samplingTimeWindow));
        pricingRepository.deleteAllByLastUpdateBetween(begin, begin.plusMinutes(samplingTimeWindow));
        pricingRepository.saveAll(summary);
    }

    /**
     * Parse a payload from the endpoint and update local data
     *
     * @param string the payload of the getBazaarItem request
     */
    public void updateBazaarPrice(String string) {
        if (completeItemHashMap == null) {
            return;
        }
        Map<String, ItemPricing> bazaarItemList = ItemPricingMapper.toBazaarItems(string);
        bazaarItemList.forEach((key, value) -> {
            if (completeItemHashMap.containsKey(key)) {
                completeItemHashMap.get(key).setPricing(value);
            }
        });
        Collection<ItemPricing> prices = bazaarItemList.values();
        pricingRepository.saveAll(prices);
        completeItemHashMap.forEach((key, value) -> computeItemMinimalCost(value));
    }

    /**
     * Resolve minimal cost value using craft if available or else buy price.
     * NB if an item have no craft and no bazaar order, the minimal cost will remain null.
     *
     * @param item a completeItem to resolve, will be call recursively if needed
     */
    private void computeItemMinimalCost(CompleteItem item) {
        if (item.getName()==null){
            logger.log(Level.WARNING,"item without name : {0} please check if item database is up to date",item);
            return;
        }
        if (CollectionsUtils.isEmpty(item.getCrafts())) {
            if (item.getPricing() == null) {
                logger.fine("no pricing for " + item.getName() + " might be missing from bazaar at this time");
                return;
            }
            if (item.getPricing().getBuyPrice() == null) {
                logger.fine("no buy price" + item.getName() + " might not have buy order at this moment");
                item.getPricing().setMinimalPrice(MINIMAL_PRICE);
                return;
            }
            item.getPricing().setMinimalPrice(item.getPricing().getBuyPrice());
            return;
        }
        for (Craft craft : item.getCrafts()) {
            try {
                updateMinimalCostUsingOneCraft(item, craft);
            } catch (InvalidObjectException e) {
                logger.log(Level.FINE, e::getMessage);
            }
        }
    }

    /**
     * Update the minimal cost by resolving the crafting cost using the given craft
     *
     * @param item  the item to update
     * @param craft one craft of the item to check
     * @throws InvalidObjectException if unable to compute craft cost or if the item have no pricing
     */
    private void updateMinimalCostUsingOneCraft(CompleteItem item, Craft craft) throws InvalidObjectException {
        if (craft.getCraftingCost() == null) {
            computeCraftCost(craft);
        }
        // check pricing after because craft cost can be useful even if there is no pricing
        if (item.getPricing() == null) {
            throw new InvalidObjectException(item.getName() + " : have no pricing");
        }
        updateMinimalPrice(item.getPricing(), craft.getCraftingCost());
    }

    /**
     * Update the minimal price value by setting it to the default (i.e. the buy cost)
     *
     * @param pricing      the pricing sheet of an item
     * @param craftingCost the cost to check with
     */
    private void updateMinimalPrice(ItemPricing pricing, Double craftingCost) {
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
     * @throws InvalidObjectException is thrown if cannot compute cost of a material
     */
    private void computeCraftCost(Craft craft) throws InvalidObjectException {
        double craftingCost = 0;
        List<String> materials = craft.getMaterialIdList();
        List<Float> quantities = craft.getQuantityOfMaterial();
        for (int i = 0; i < materials.size(); i++) {
            String materialId = materials.get(i);
            float quantity = quantities.get(i);
            craftingCost += extractCostForAMaterial(materialId) * quantity;
        }
        craft.setCraftingCost(craftingCost);
    }

    /**
     * Extract the cost of a material multiplied
     *
     * @param materialId id of a material to find
     * @return the extracted value
     * @throws InvalidObjectException is thrown when a material have no pricing
     */
    private double extractCostForAMaterial(String materialId) throws InvalidObjectException {
        if (!completeItemHashMap.containsKey(materialId)) {
            throw new InvalidObjectException(materialId.concat(" : is missing from local database"));
        }
        CompleteItem completeItem = completeItemHashMap.get(materialId);
        ItemPricing itemPricing = completeItem.getPricing();
        double minimalPrice;
        if (itemPricing == null) {
            minimalPrice = handleMissingPricing(completeItem);
        } else {
            if (itemPricing.getMinimalPrice() == null) {
                computeItemMinimalCost(completeItem);
            }
            if (itemPricing.getMinimalPrice()==null){
                throw new InvalidObjectException(materialId.concat(" : corrupted item, check its craft"));
            }
            minimalPrice = itemPricing.getMinimalPrice();
        }
        return minimalPrice;
    }

    private double handleMissingPricing(CompleteItem completeItem) throws InvalidObjectException {
        if (CollectionsUtils.isNotEmpty(completeItem.getCrafts())) {
            if (completeItem.getCrafts().get(0).getCraftingCost() == null) {
                computeItemMinimalCost(completeItem);
            }
            return completeItem.getCrafts().get(0).getCraftingCost();
        } else {
            throw new InvalidObjectException(completeItem.getName() + " : no pricing and no craft, craft aborted");
        }
    }

}
