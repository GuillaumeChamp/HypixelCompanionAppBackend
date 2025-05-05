package com.example.hypixeltrackerbackend.services;


import com.example.hypixeltrackerbackend.data.constant.TimeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SchedulerService {
    private final BazaarDataProcessorService bazaarDataProcessorService;
    private final Logger logger = Logger.getLogger(SchedulerService.class.getName());
    private boolean isRunning = false;
    private ScheduledExecutorService scheduleTaskExecutor;
    private final ApiFetcherService apiFetcherService;

    @Autowired
    public SchedulerService(BazaarDataProcessorService bazaarDataProcessorService, ApiFetcherService apiFetcherService) {
        this.bazaarDataProcessorService = bazaarDataProcessorService;
        this.apiFetcherService = apiFetcherService;
    }

    /**
     * Start the routine task, checking if not already launched
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException {
        if (isRunning) {
            return;
        }
        isRunning = true;
        scheduleTaskExecutor = Executors.newScheduledThreadPool(3);
        bazaarDataProcessorService.preloadData();
        scheduleTaskExecutor.scheduleAtFixedRate(this::processNewestData, 0, TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS);
        scheduleTaskExecutor.scheduleAtFixedRate(bazaarDataProcessorService::deleteLastYearRecords, 0, 1, TimeUnit.DAYS);
        scheduleTaskExecutor.scheduleAtFixedRate(() -> bazaarDataProcessorService.groupOneHourRecords(LocalDateTime.now().minusHours(2)), 1, 1, TimeUnit.HOURS);
        scheduleTaskExecutor.scheduleAtFixedRate(() -> bazaarDataProcessorService.groupOneDayRecords(LocalDateTime.now().minusDays(2)), 1, 1, TimeUnit.DAYS);
        scheduleTaskExecutor.scheduleAtFixedRate(() -> bazaarDataProcessorService.groupOneWeekRecords(LocalDateTime.now().minusWeeks(2)), 14, 7, TimeUnit.DAYS);
        logger.log(Level.INFO, "Scheduler started !");
    }

    private void processNewestData() {
        try {
            String response = apiFetcherService.getBazaar();
            bazaarDataProcessorService.updateBazaarPrice(response);
        } catch (Exception e) {
            logger.log(Level.WARNING, parseException(e));
        }
    }

    public void stop() {
        this.isRunning = false;
        scheduleTaskExecutor.shutdownNow();
        logger.log(Level.INFO, "Scheduler stopped !");
    }

    public boolean isStarted() {
        return this.scheduleTaskExecutor != null && this.isRunning;
    }

    private String parseException(Exception e) {
        return "Error while processing bazaar request : [" + e.getClass().getName() + "] " + e.getMessage();
    }
}
