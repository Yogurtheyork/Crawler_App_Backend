package com.example.backend.service;

import com.example.backend.crawler.KeelungSightsCrawler;
import com.example.backend.model.Sight;
import com.example.backend.repository.SightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class SightServiceImpl implements SightService {

    private final SightRepository sightRepository;
    private final KeelungSightsCrawler sightCrawler;
    private final List<String> zones = List.of("中山", "信義", "仁愛", "安樂", "中正", "暖暖", "七堵");

    @Autowired
    public SightServiceImpl(SightRepository sightRepository) throws IOException {
        this.sightRepository = sightRepository;
        this.sightCrawler = new KeelungSightsCrawler();
    }

    @Override
    public void initializeDatabase() throws IOException {
        deleteAllSights();
        for (String zone : zones) {
            System.out.println("Fetching sights for zone: " + zone);
            var sights = sightCrawler.getItems(zone);
            for (var sight : sights) {
                saveSight(sight);
                System.out.println("Saving sight: " + sight.getSightName());
            }
            System.out.println("Finished fetching sights for zone: " + zone);
        }
        System.out.println("All sights have been fetched and saved to the database.");
    }

    @Override
    public void refreshDatabase() {
        if (sightRepository.count() == 0) {
            try {
                initializeDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveSight(Sight sight) {
        sightRepository.save(sight);
    }

    @Override
    public void deleteAllSights() {
        sightRepository.deleteAll();
    }

    @Override
    public List<Sight> getSightsByZone(String zone) {
        return sightRepository.findByZone(zone);
    }
}