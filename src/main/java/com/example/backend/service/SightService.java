package com.example.backend.service;

import com.example.backend.model.Sight;
import java.io.IOException;
import java.util.List;

public interface SightService {
    void initializeDatabase() throws IOException;
    void saveSight(Sight sight);
    void deleteAllSights();
    void refreshDatabase();
    List<Sight> getSightsByZone(String zone);
}