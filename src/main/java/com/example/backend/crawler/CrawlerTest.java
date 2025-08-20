package com.example.backend.crawler;

import com.example.backend.model.Sight;
import java.io.IOException;

public class CrawlerTest {

    public static void main(String[] args) throws IOException {
        KeelungSightsCrawler crawler = new KeelungSightsCrawler();
        Sight[] sights = crawler.getItems("七堵");
        for (Sight s: sights) {
            System.out.println(s);
        }
    }
}
