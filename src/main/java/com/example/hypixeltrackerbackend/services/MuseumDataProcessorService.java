package com.example.hypixeltrackerbackend.services;

import com.example.hypixeltrackerbackend.data.museum.MuseumItem;
import com.example.hypixeltrackerbackend.data.museum.MuseumRecordAdapter;
import com.example.hypixeltrackerbackend.data.repositories.MuseumRecordsRepository;
import com.example.hypixeltrackerbackend.utils.mapper.MuseumItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MuseumDataProcessorService {
    private final MuseumRecordsRepository repository;
    private List<MuseumItem> museumItems;

    @Autowired
    public MuseumDataProcessorService(MuseumRecordsRepository repository) {
        this.repository = repository;
    }

    public boolean isARecordExistsForAProfileID(String profileID) {
        return repository.existsById(profileID);
    }

    public boolean isARecentRecodeExistsForAProfileID(String profileID) {
        return repository.existsByProfileIDAndLastUpdateAfter(profileID, MuseumRecordAdapter.getRecentLowerLimit());
    }

    public Set<String> getMuseumIdsForAProfileID(String profileID) {
        return new MuseumRecordAdapter(repository.getByProfileID(profileID)).getMuseumIds();
    }

    public List<MuseumItem> getStaticMuseumItems() {
        if (museumItems == null) {
            museumItems = MuseumItemMapper.generateMuseumItemList();
        }
        return museumItems;
    }
}
