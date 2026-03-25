package com.fetalynx.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fetalynx.model.Reading;
import com.fetalynx.service.ReadingService;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<Reading>> getReadings(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "300") int limit) {
        return ResponseEntity.ok(readingService.getReadings(patientId, limit));
    }

    @GetMapping("/{patientId}/latest")
    public ResponseEntity<List<Reading>> getLatest(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "50") int count) {
        return ResponseEntity.ok(readingService.getLatestReadings(patientId, count));
    }

    @GetMapping("/{patientId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "300") int window) {
        return ResponseEntity.ok(readingService.getAggregatedStats(patientId, window));
    }
}
