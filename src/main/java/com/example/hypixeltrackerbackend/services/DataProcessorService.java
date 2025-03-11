package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.constant.TimeConstant;
import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import com.example.hypixeltrackerbackend.data.bazaar.ItemPricing;
import com.example.hypixeltrackerbackend.data.mapper.ItemPricingMapper;
import com.example.hypixeltrackerbackend.data.mapper.StaticItemMapper;
import com.example.hypixeltrackerbackend.repository.ItemPricingRepository;
import com.example.hypixeltrackerbackend.utils.ItemPricingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DataProcessorService {
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
     *
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
     *
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
        logger.log(Level.INFO, logString);
    }

    private void groupRecordsWithTimeStampAndWindowSize(LocalDateTime begin, Integer samplingTimeWindow) {
        List<ItemPricing> summary = pricingRepository.groupAllByTimestampBetween(begin, begin.plusMinutes(samplingTimeWindow));
        pricingRepository.deleteAllInBatchByLastUpdateBetween(begin, begin.plusMinutes(samplingTimeWindow));
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
        ItemPricingUtil.updateAllItemsMinimalCost(completeItemHashMap);
    }

}
