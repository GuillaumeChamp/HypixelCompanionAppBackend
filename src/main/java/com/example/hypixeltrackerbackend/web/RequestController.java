package com.example.hypixeltrackerbackend.web;

import com.example.hypixeltrackerbackend.data.bazaar.CompleteItem;
import com.example.hypixeltrackerbackend.data.museum.MuseumItem;
import com.example.hypixeltrackerbackend.services.MuseumDataProcessorService;
import com.example.hypixeltrackerbackend.web.responses.PricingRecord;
import com.example.hypixeltrackerbackend.web.responses.UUIDResponse;
import com.example.hypixeltrackerbackend.services.BazaarDataProcessorService;
import com.example.hypixeltrackerbackend.web.exceptions.HTTPRequestException;
import com.example.hypixeltrackerbackend.services.ApiFetcherService;
import com.example.hypixeltrackerbackend.utils.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;


@RestController
public class RequestController {
    private final BazaarDataProcessorService bazaarDataProcessorService;
    private final ApiFetcherService apiFetcherService;
    private final MuseumDataProcessorService museumDataProcessorService;

    @Autowired
    public RequestController(BazaarDataProcessorService bazaarDataProcessorService, ApiFetcherService apiFetcherService, MuseumDataProcessorService museumDataProcessorService) {
        this.bazaarDataProcessorService = bazaarDataProcessorService;
        this.apiFetcherService = apiFetcherService;
        this.museumDataProcessorService = museumDataProcessorService;
    }

    /*
    Bazaar Workflow
     */

    @CrossOrigin
    @GetMapping("/bazaar")
    List<CompleteItem> current() {
        return bazaarDataProcessorService.getLastData().values().stream().toList();
    }

    @CrossOrigin
    @GetMapping(value = {"/bazaar/{id}", "/bazaar/{id}/{window}"})
    List<PricingRecord> getHistory(@PathVariable("id") String itemId, @PathVariable(value = "window", required = false) String timeWindow) {
        List<PricingRecord> history = bazaarDataProcessorService.getHistory(itemId, timeWindow).stream().map(PricingRecord::new).toList();
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
                bazaarDataProcessorService.groupOneDayRecords(now.minusWeeks(i));
            }
            // compress last week
            for (int i = 2; i < 7; i++) {
                bazaarDataProcessorService.groupOneDayRecords(now.minusDays(i));
            }
            // compress last day
            for (int i = 2; i < 24; i++) {
                bazaarDataProcessorService.groupOneHourRecords(now.minusHours(i));
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
            return museumDataProcessorService.getStaticMuseumItems();
        } catch (MissingResourceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get Museum Items");
        }
    }

    /**
     * send a set containing uuid of all items in the museum of the given profile
     * @param profile uuid of the profile
     * @return there is three scenario
     * First, if a previous record exist in the database and is recent return this records
     * Else, query to the provider api the current state of the museum
     * Finally, if the api key is invalid and if a previous record exist send it
     * @throws ResponseStatusException if the final case failed
     */
    @CrossOrigin
    @GetMapping("/museum/{profile}")
    Set<String> getMuseumItemsForAProfile(@PathVariable String profile) throws ResponseStatusException {
        if (museumDataProcessorService.isARecentRecodeExistsForAProfileID(profile)) {
            return museumDataProcessorService.getMuseumIdsForAProfileID(profile);
        }
        try {
             return apiFetcherService.getMuseumItemsForAProfile(profile);
        } catch (HTTPRequestException e) {
            if (HttpStatus.FORBIDDEN.equals(e.getStatusCode())
                    && museumDataProcessorService.isARecordExistsForAProfileID(profile)) {
                        return museumDataProcessorService.getMuseumIdsForAProfileID(profile);
            }
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping(value = {"/uuid/{username}"})
    UUIDResponse getPlayerUUID(@PathVariable("username") String username) {
        try {
            return apiFetcherService.getUUIDFromUsername(username);
        } catch (HTTPRequestException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping(value = {"/profiles/{playerUUID}"})
    Map<String, String> getProfilesNameByPlayer(@PathVariable("playerUUID") String playerUUID) {
        try {
            return apiFetcherService.getProfilesByPlayerUUID(playerUUID);
        } catch (HTTPRequestException e) {
            throw new ResponseStatusException(e.getStatusCode(), "Invalid playerUUID : " + playerUUID);
        }
    }

}
