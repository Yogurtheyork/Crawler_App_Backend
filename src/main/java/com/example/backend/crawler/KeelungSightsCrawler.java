package com.example.backend.crawler;

import com.example.backend.model.Sight;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class KeelungSightsCrawler {
    private final Document doc;
    private final ExecutorService executorService;
    private static final int TIMEOUT = 30000;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 5000; // 5 seconds delay between retries

    public KeelungSightsCrawler() throws IOException {
        this.doc = Jsoup.connect("https://www.travelking.com.tw/tourguide/taiwan/keelungcity/")
                .timeout(TIMEOUT)
                .get();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    // Fetch sight details with retries
    private Sight fetchSight(String url, String zone, int retries) {
        for (int attempt = 0; attempt < retries; attempt++) {
            try {
                Document sightPage = Jsoup.connect(url)
                        .timeout(TIMEOUT)
                        .get();

                Sight sight = new Sight();
                sight.setSightName(sightPage.select("meta[itemprop=name]").attr("content"));
                sight.setZone(zone + "區");
                sight.setCategory(sightPage.select("span[property=rdfs:label]").text());
                sight.setPhotoURL(sightPage.select("meta[itemprop=image]").attr("content"));
                if (sight.getPhotoURL().isEmpty()) {
                    sight.setPhotoURL("https://upload.wikimedia.org/wikipedia/commons/a/a3/Image-not-found.png?20210521171500");
                }
                sight.setDescription(sightPage.select("meta[itemprop=description]").attr("content"));
                sight.setAddress(sightPage.select("meta[itemprop=address]").attr("content"));
                return sight;
            } catch (IOException e) {
                if (attempt < retries - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ie);
                    }
                } else {
                    throw new RuntimeException("Failed to fetch sight after " + retries + " attempts", e);
                }
            }
        }
        return null;
    }

    public Sight[] getItems(String zone) {
        Elements districts = doc.select("#guide-point h4");
        List<Sight> sightsList = new ArrayList<>();

        for (Element district : districts) {
            if (district.text().equals(zone + "區")) {
                Element ul = district.nextElementSibling();
                if (ul != null && ul.tagName().equals("ul")) {
                    Elements lists = ul.select("a");

                    List<CompletableFuture<Sight>> futures = lists.stream()
                            .map(list -> CompletableFuture.supplyAsync(() ->
                                            fetchSight("https://www.travelking.com.tw" + list.attr("href"),
                                                    zone,
                                                    MAX_RETRIES),
                                    executorService))
                            .toList();

                    try {
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                .orTimeout(TIMEOUT * 2, TimeUnit.MILLISECONDS)
                                .join();

                        sightsList = futures.stream()
                                .map(CompletableFuture::join)
                                .filter(sight -> sight != null)
                                .toList();
                    } catch (CompletionException e) {
                        // If timeout occurs, retry the entire zone
                        return getItems(zone);
                    }
                }
            }
        }

        return sightsList.toArray(new Sight[0]);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}