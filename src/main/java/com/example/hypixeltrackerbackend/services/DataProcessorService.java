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
    private static final String SUCCESSFULLY_COMPRESS_DATA_FROM_1_TO_2 = "Successfully compress data from {1} to {2}";


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
            case TimeConstant.DAY_TIME_WINDOW -> ending.minusHours(24);
            case TimeConstant.WEEK_TIME_WINDOW -> ending.minusDays(7);
            case TimeConstant.MOUTH_TIME_WINDOW -> ending.minusMonths(1);
            case TimeConstant.YEAR_TIME_WINDOW -> ending.minusYears(1);
            case null, default -> ending.minusHours(1);
        };
        List<ItemPricing> answer = pricingRepository.findAllByItemIdAndTimeBetweenOrderByTime(itemId, beginning, ending);
        logger.log(Level.INFO, () -> "successfully processed history for " + itemId + " for " + timeWindow);
        return answer;
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
        logger.log(Level.INFO, SUCCESSFULLY_COMPRESS_DATA_FROM_1_TO_2,new Object[]{beginning,startOfWindow});
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
        logger.log(Level.INFO, SUCCESSFULLY_COMPRESS_DATA_FROM_1_TO_2,new Object[]{beginning,startOfWindow});
    }

    /**
     * Group all the records from the given date time and the given date time plus one day
     *
     * @param beginning the beginning of the compression
     * @see TimeConstant
     */
    @Transactional
    public void groupOneWeekRecords(LocalDateTime beginning) {
        LocalDateTime startOfWindow = beginning.truncatedTo(ChronoUnit.SECONDS);

        for (int i = 0; i < TimeConstant.VALUES_BY_WEEK; i++) {
            groupRecordsWithTimeStampAndWindowSize(startOfWindow, TimeConstant.SAMPLING_BY_WEEK_TIME_SLOT_IN_MINUTES);
            startOfWindow = startOfWindow.plusMinutes(TimeConstant.SAMPLING_BY_WEEK_TIME_SLOT_IN_MINUTES);
        }
        logger.log(Level.INFO, SUCCESSFULLY_COMPRESS_DATA_FROM_1_TO_2,new Object[]{beginning,startOfWindow});
    }

    private void groupRecordsWithTimeStampAndWindowSize(LocalDateTime begin, Integer samplingTimeWindow) {
        List<ItemPricing> summary = pricingRepository.groupAllByTimestampBetween(begin, begin.plusMinutes(samplingTimeWindow));
        pricingRepository.deleteAllInBatchByTimeBetween(begin, begin.plusMinutes(samplingTimeWindow));
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
