package com.example.hypixeltrackerbackend.services;


import com.example.hypixeltrackerbackend.constant.TimeConstant;
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
    private final DataProcessorService dataProcessorService;
    private final Logger logger = Logger.getLogger(SchedulerService.class.getName());
    private boolean isRunning = false;
    private ScheduledExecutorService scheduleTaskExecutor;


    @Autowired
    public SchedulerService(DataProcessorService dataProcessorService) {
        this.dataProcessorService = dataProcessorService;
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

        logger.log(Level.INFO, "Scheduler started !");
        dataProcessorService.preloadData();
        scheduleTaskExecutor.scheduleAtFixedRate(() -> {
            String response = DataFetcher.queryBazaarData();
            if (response == null) {
                logger.warning("no response received or task canceled");
                return;
            }
            dataProcessorService.updateBazaarPrice(response);
        }, 0, TimeConstant.CALL_FREQUENCY_IN_SECOND, TimeUnit.SECONDS);
        scheduleTaskExecutor.scheduleAtFixedRate(()->dataProcessorService.groupOneHourRecords(LocalDateTime.now().minusHours(2)), 1, 1, TimeUnit.HOURS);
        scheduleTaskExecutor.scheduleAtFixedRate(()->dataProcessorService.groupOneDayRecords(LocalDateTime.now().minusDays(2)), 1, 1, TimeUnit.DAYS);

    }

    public void stop() {
        this.isRunning = false;
        scheduleTaskExecutor.shutdownNow();
        logger.log(Level.INFO, "Scheduler stopped !");
    }

    public boolean isStarted(){
        return this.scheduleTaskExecutor !=null && this.isRunning;
    }
}
