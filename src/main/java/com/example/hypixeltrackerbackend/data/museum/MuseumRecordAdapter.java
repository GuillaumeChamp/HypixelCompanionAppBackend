package com.example.hypixeltrackerbackend.data.museum;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Facade offering a more developer friendly way to access Museum Record
 */
public class MuseumRecordAdapter {
    private final String profileId;
    private final Set<String> museumIds;
    private final LocalDateTime lastUpdated;

    public MuseumRecordAdapter(String profileId, Set<String> museumIds) {
        this.profileId = profileId;
        this.museumIds = museumIds;
        this.lastUpdated = LocalDateTime.now();
    }

    public MuseumRecordAdapter(MuseumRecord museumRecord) {
        this.profileId = museumRecord.getProfileID();
        this.museumIds = new HashSet<>();
        this.museumIds.addAll(List.of(museumRecord.getMuseumIDs().split(",")));
        this.lastUpdated = museumRecord.getLastUpdate();
    }

    public MuseumRecord getMuseumRecord() {
        return new MuseumRecord(profileId, String.join(",", this.museumIds), lastUpdated);
    }

    public boolean isRecent(){
        return this.lastUpdated.isAfter(LocalDateTime.now().minusMinutes(10));
    }

    public Set<String> getMuseumIds() {
        return museumIds;
    }
}
