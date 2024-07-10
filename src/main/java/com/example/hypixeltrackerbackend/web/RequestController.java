package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.data.CompleteItem;
import com.example.hypixeltrackerbackend.data.PricingRecord;
import com.example.hypixeltrackerbackend.services.DataProcessorService;
import com.example.hypixeltrackerbackend.utils.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;


@RestController
public class RequestController {
    private static final String DEFAULT_TIME_WINDOW = "hour";
    private final DataProcessorService dataProcessorService;

    @Autowired
    public RequestController(DataProcessorService dataProcessorService) {
        this.dataProcessorService = dataProcessorService;
    }

    @CrossOrigin
    @GetMapping("/bazaar")
    Map<String, CompleteItem> current() {
        return dataProcessorService.getLastData();
    }

    @CrossOrigin
    @GetMapping(value = {"/bazaar/{id}", "/bazaar/{id}/{window}"})
    List<PricingRecord> getHistory(@PathVariable("id") String itemId, @PathVariable(value = "window", required = false) String timeWindow) {
        timeWindow = timeWindow==null ? DEFAULT_TIME_WINDOW : timeWindow;
        List<PricingRecord> history = StreamSupport.stream(dataProcessorService.getHistory(itemId, timeWindow).spliterator(), false).map(PricingRecord::new).toList();
        if (CollectionsUtils.isEmpty(history)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found : " + itemId);
        }
        return history;
    }

}
