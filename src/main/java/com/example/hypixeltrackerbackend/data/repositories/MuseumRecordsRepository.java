package com.example.hypixeltrackerbackend.data.repositories;

import com.example.hypixeltrackerbackend.data.museum.MuseumRecord;
import com.example.hypixeltrackerbackend.data.museum.MuseumRecordAdapter;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MuseumRecordsRepository extends CrudRepository<MuseumRecord, String> {
    default void save(MuseumRecordAdapter museumRecordAdapter){
        save(museumRecordAdapter.getMuseumRecord());
    }

    Optional<MuseumRecord> getByProfileID(String profileID);
}
