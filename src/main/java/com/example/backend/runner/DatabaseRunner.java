package com.example.backend.runner;

import com.example.backend.service.SightService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class DatabaseRunner implements ApplicationRunner {

    private final SightService sightService;

    public DatabaseRunner(SightService sightService) {
        this.sightService = sightService;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws IOException {
        sightService.initializeDatabase();
    }
}