package com.fetalynx.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fetalynx.model.Reading;

@Service
public class ReadingService {

    private final Map<String, List<Reading>> readingsByPatient = new ConcurrentHashMap<>();

    public Reading addReading(String patientId, double doppler, double piezo, long timestamp) {
        Reading reading = new Reading(patientId, doppler, piezo, timestamp);
        readingsByPatient
                .computeIfAbsent(patientId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(reading);
        return reading;
    }

    public List<Reading> getReadings(String patientId, int limit) {
        List<Reading> readings = readingsByPatient.getOrDefault(patientId, Collections.emptyList());
        if (limit > 0 && readings.size() > limit) {
            return readings.subList(readings.size() - limit, readings.size());
        }
        return new ArrayList<>(readings);
    }

    public List<Reading> getLatestReadings(String patientId, int count) {
        return getReadings(patientId, count);
    }

    public Map<String, Object> getAggregatedStats(String patientId, int windowSize) {
        List<Reading> readings = getReadings(patientId, windowSize);
        if (readings.isEmpty()) {
            return Map.of("count", 0);
        }

        double avgDoppler = readings.stream().mapToDouble(Reading::getDoppler).average().orElse(0);
        double avgPiezo = readings.stream().mapToDouble(Reading::getPiezo).average().orElse(0);
        double maxDoppler = readings.stream().mapToDouble(Reading::getDoppler).max().orElse(0);
        double maxPiezo = readings.stream().mapToDouble(Reading::getPiezo).max().orElse(0);
        double minDoppler = readings.stream().mapToDouble(Reading::getDoppler).min().orElse(0);
        double minPiezo = readings.stream().mapToDouble(Reading::getPiezo).min().orElse(0);

        // Calculate variability (standard deviation)
        double dopplerVariability = Math.sqrt(readings.stream()
                .mapToDouble(r -> Math.pow(r.getDoppler() - avgDoppler, 2))
                .average().orElse(0));

        double piezoVariability = Math.sqrt(readings.stream()
                .mapToDouble(r -> Math.pow(r.getPiezo() - avgPiezo, 2))
                .average().orElse(0));

        Map<String, Object> stats = new HashMap<>();
        stats.put("count", readings.size());
        stats.put("avgDoppler", Math.round(avgDoppler * 100.0) / 100.0);
        stats.put("avgPiezo", Math.round(avgPiezo * 100.0) / 100.0);
        stats.put("maxDoppler", maxDoppler);
        stats.put("maxPiezo", maxPiezo);
        stats.put("minDoppler", minDoppler);
        stats.put("minPiezo", minPiezo);
        stats.put("dopplerVariability", Math.round(dopplerVariability * 100.0) / 100.0);
        stats.put("piezoVariability", Math.round(piezoVariability * 100.0) / 100.0);

        return stats;
    }

    public int getReadingCount(String patientId) {
        return readingsByPatient.getOrDefault(patientId, Collections.emptyList()).size();
    }
}
