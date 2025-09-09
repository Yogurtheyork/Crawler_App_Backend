package com.example.backend.runner;

import com.example.backend.crawler.KeelungSightsCrawler;
import com.example.backend.repository.SightRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseRunner implements ApplicationRunner{

    private final SightRepository sightRepository;
    private final KeelungSightsCrawler sightCrawler;

    public DatabaseRunner(SightRepository sightRepository) throws IOException {
        this.sightRepository = sightRepository;
        this.sightCrawler = new KeelungSightsCrawler();
    }
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws IOException {
        for (String zone : new String[]{"中山", "信義", "仁愛", "安樂", "中正", "暖暖", "七堵"}) {
            try {
                System.out.println("Fetching sights for zone: " + zone);
                var sights = sightCrawler.getItems(zone);
                for (var sight : sights) {
                    System.out.println("Saving sight: " + sight.getSightName());
                    sightRepository.save(sight);
                }
                System.out.println("Finished fetching sights for zone: " + zone);
            } catch (IOException e) {
                System.err.println("Error fetching sights for zone: " + zone);
            }
        }
        System.out.println("All sights have been fetched and saved to the database.");
    }
}
