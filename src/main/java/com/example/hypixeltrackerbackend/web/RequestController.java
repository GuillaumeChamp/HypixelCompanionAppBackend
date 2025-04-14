package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import com.example.hypixeltrackerbackend.data.museum.MuseumItem;
import com.example.hypixeltrackerbackend.data.mapper.MuseumItemMapper;
import com.example.hypixeltrackerbackend.data.bazaar.PricingRecord;
import com.example.hypixeltrackerbackend.data.responses.UUIDResponse;
import com.example.hypixeltrackerbackend.services.DataProcessorService;
import com.example.hypixeltrackerbackend.services.exceptions.HTTPRequestException;
import com.example.hypixeltrackerbackend.services.ApiFetcherService;
import com.example.hypixeltrackerbackend.utils.CollectionsUtils;
import com.example.hypixeltrackerbackend.utils.request_parsers.ProfilesRequestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
public class RequestController {
    private final DataProcessorService dataProcessorService;
    private final ApiFetcherService apiFetcherService;

    @Autowired
    public RequestController(DataProcessorService dataProcessorService, ApiFetcherService apiFetcherService) {
        this.dataProcessorService = dataProcessorService;
        this.apiFetcherService = apiFetcherService;
    }

    /*
    Bazaar Workflow
     */

    @CrossOrigin
    @GetMapping("/bazaar")
    List<CompleteItem> current() {
        return dataProcessorService.getLastData().values().stream().toList();
    }

    @CrossOrigin
    @GetMapping(value = {"/bazaar/{id}", "/bazaar/{id}/{window}"})
    List<PricingRecord> getHistory(@PathVariable("id") String itemId, @PathVariable(value = "window", required = false) String timeWindow) {
        List<PricingRecord> history = dataProcessorService.getHistory(itemId, timeWindow).stream().map(PricingRecord::new).toList();
        if (CollectionsUtils.isEmpty(history)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found : " + itemId);
        }
        return history;
    }

    @CrossOrigin
    @GetMapping(value = {"/bazaar/compress"})
    String compressData() {
        LocalDateTime now = LocalDateTime.now();
        new Thread(() -> {
            // compress last year
            for (int i = 2; i < 52; i++) {
                dataProcessorService.groupOneDayRecords(now.minusWeeks(i));
            }
            // compress last week
            for (int i = 2; i < 7; i++) {
                dataProcessorService.groupOneDayRecords(now.minusDays(i));
            }
            // compress last day
            for (int i = 2; i < 24; i++) {
                dataProcessorService.groupOneHourRecords(now.minusHours(i));
            }
        }).start();
        return "compressing data...";
    }

    /*
    Museum Workflow
     */

    @CrossOrigin
    @GetMapping("/museum")
    List<MuseumItem> getMuseumItems() {
        try {
            return MuseumItemMapper.generateMuseumItemList();
        } catch (IOException io) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get Museum Items");
        }
    }

    @CrossOrigin
    @GetMapping(value = {"/uuid/{username}"})
    UUIDResponse getPlayerUUID(@PathVariable("username") String username) {
        try {
            return apiFetcherService.getUUIDFromUsername(username);
        } catch (HTTPRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username : " + username);
        }
    }

    @CrossOrigin
    @GetMapping(value = {"/profiles/{playerUUID}"})
    Map<String, String> getProfilesNameByPlayer(@PathVariable("playerUUID") String playerUUID) {
        try {
            String profilesPayload = apiFetcherService.getProfilesByPlayerUUID(playerUUID);
            return ProfilesRequestParser.extractProfilesNames(profilesPayload);
        } catch (HTTPRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playerUUID : " + playerUUID);
        }
    }

}
