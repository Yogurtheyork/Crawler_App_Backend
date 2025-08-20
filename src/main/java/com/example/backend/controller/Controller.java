package com.example.backend.controller;

import com.example.backend.crawler.KeelungSightsCrawler;
import com.example.backend.model.Sight;
import com.example.backend.repository.SightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
public class Controller {

    @Autowired
    private SightRepository sightRepository;
    // Test ping
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.status(HttpStatus.OK).body("Hello world");
    }

    @GetMapping("/crawlerTest")
    public Sight[] crawlerTest() throws IOException {
        KeelungSightsCrawler crawler = new KeelungSightsCrawler();
        Sight[] sights = crawler.getItems("七堵");
        return sights;
    }

    @GetMapping("/SightAPI")
    public ResponseEntity<List<Sight>> sightStatus(
            @RequestParam(value = "zone", required = true) String zone
    ) throws IOException {
        List<Sight> sights = sightRepository.findByZone(zone + "區");
        return sights == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(sights);
    }

    @GetMapping("/SightAPI/testDB")
    public ResponseEntity<List<Sight>> sightCategory(){
        List<Sight> sights = sightRepository.findByZone("中山區");
        return sights == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(sights);
    }


}
