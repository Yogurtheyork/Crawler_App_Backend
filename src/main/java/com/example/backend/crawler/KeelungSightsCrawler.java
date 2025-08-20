package com.example.backend.crawler;

import com.example.backend.model.Sight;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class KeelungSightsCrawler {

    Document doc;

    public KeelungSightsCrawler() throws IOException {
        doc = Jsoup.connect("https://www.travelking.com.tw/tourguide/taiwan/keelungcity/").get();
    }

    public Sight[] getItems(String zone) throws IOException {
        Elements districts = doc.select("#guide-point h4");
        Sight[] sights = null;
        for (Element district : districts) {
            if (district.text().equals(zone + "區")) {
                Element ul = district.nextElementSibling();

                if (ul != null && ul.tagName().equals("ul")) {
                    Elements lists = ul.select("a");

                    sights = new Sight[lists.size()];

                    for (Element list : lists) {
                        System.out.println("Processing sight: " + list.text());
                        Sight sight = new Sight();
                        Document sightPage = Jsoup.connect("https://www.travelking.com.tw" + list.attr("href")).get();
                        sight.setSightName(sightPage.select("meta[itemprop=name]").attr("content"));
                        sight.setZone(zone + "區");
                        sight.setCategory(sightPage.select("span[property=rdfs:label]").text());
                        sight.setPhotoURL(sightPage.select("meta[itemprop=image]").attr("content"));
                        sight.setDescription(sightPage.select("meta[itemprop=description]").attr("content"));
                        sight.setAddress(sightPage.select("meta[itemprop=address]").attr("content"));
                        sights[lists.indexOf(list)] = sight; // Store the sight in the array
                    }

                }
            }
        }
        return sights; // Return an empty array if no sights found
    }
}
