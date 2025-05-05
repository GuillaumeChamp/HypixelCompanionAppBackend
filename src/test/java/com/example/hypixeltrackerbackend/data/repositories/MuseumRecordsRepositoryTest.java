package com.example.hypixeltrackerbackend.data.repositories;

import com.example.hypixeltrackerbackend.data.museum.MuseumRecord;
import com.example.hypixeltrackerbackend.data.museum.MuseumRecordAdapter;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MuseumRecordsRepositoryTest {
    @Autowired
    private MuseumRecordsRepository repository;

    @Test
    void shouldSaveMuseumRecord() {
        MuseumRecord museumRecord = new MuseumRecord("TEST", "Museum 1, Museum 2, Museum 3", LocalDateTime.now());
        repository.save(museumRecord);

        assertThat(repository.findById("TEST"))
                .get()
                .extracting(MuseumRecord::getMuseumIDs).asString()
                .contains("Museum 1", "Museum 2", "Museum 3");
    }
    @Test
    void shouldSaveMuseumRecordFacade() {
        MuseumRecordAdapter museumRecord = new MuseumRecordAdapter("TEST", Set.of("Museum 4", "Museum 5", "Museum 6"));
        repository.save(museumRecord);

        assertThat(repository.findById("TEST"))
                .get()
                .extracting(MuseumRecordAdapter::new)
                .extracting("museumIds")
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION).containsAll(List.of("Museum 4", "Museum 5", "Museum 6"));
    }
}