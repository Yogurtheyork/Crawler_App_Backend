package com.example.backend.controller;

import com.example.backend.crawler.KeelungSightsCrawler;
import com.example.backend.model.Sight;
import com.example.backend.repository.SightRepository;
import com.example.backend.service.SightServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
public class Controller {
    @Autowired
    private SightServiceImpl sightService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running");
    }

    @GetMapping("/SightAPI")
    public ResponseEntity<List<Sight>> sightStatus(
            @RequestParam(value = "zone", required = true) String zone
    ) {
        try {
            sightService.refreshDatabase();
            List<Sight> sight = sightService.getSightsByZone(zone + "區");
            if(sight.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(sight);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
