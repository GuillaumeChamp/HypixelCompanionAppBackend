package com.example.hypixeltrackerbackend.data.museum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Record of museum items for one user
 * Do not use it raw, use adapter instead
 * @see MuseumRecordAdapter
 */
@Entity
@Table(name = "museum_records")
public class MuseumRecord {
    @Id
    private String profileID;
    @Column(columnDefinition = "CLOB")
    private String museumIDs;
    private LocalDateTime lastUpdate;

    public MuseumRecord() {
    }

    public MuseumRecord(String profileID, String museumIDs, LocalDateTime lastUpdate) {
        this.profileID = profileID;
        this.museumIDs = museumIDs;
        this.lastUpdate = lastUpdate;
    }

    public String getProfileID() {
        return profileID;
    }

    public String getMuseumIDs() {
        return museumIDs;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
