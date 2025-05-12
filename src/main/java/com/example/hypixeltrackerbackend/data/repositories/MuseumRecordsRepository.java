package com.example.hypixeltrackerbackend.data.repositories;

import com.example.hypixeltrackerbackend.data.museum.MuseumRecord;
import com.example.hypixeltrackerbackend.data.museum.MuseumRecordAdapter;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface MuseumRecordsRepository extends CrudRepository<MuseumRecord, String> {
    default void save(MuseumRecordAdapter museumRecordAdapter){
        save(museumRecordAdapter.getMuseumRecord());
    }

    /**
     * Guaranty to find only one because there is only one record by ID
     * @param profileID the profile ID
     * @return a record of its museum if found
     */
    MuseumRecord getByProfileID(String profileID);
    boolean existsByProfileIDAndLastUpdateAfter(String profileID, LocalDateTime lastUpdateAfter);
}
