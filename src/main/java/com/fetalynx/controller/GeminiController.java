package com.fetalynx.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fetalynx.service.GeminiService;
import com.fetalynx.service.ReadingService;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;
    private final ReadingService readingService;

    public GeminiController(GeminiService geminiService, ReadingService readingService) {
        this.geminiService = geminiService;
        this.readingService = readingService;
    }

    /**
     * Generate a Gemini AI report for a patient.
     * Expects: { "patientId": "...", "gestationalAge": "32 weeks", "notes": "..." }
     */
    @PostMapping("/analyse")
    public ResponseEntity<?> analyse(@RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        String gestationalAge = body.getOrDefault("gestationalAge", "Unknown");
        String notes = body.getOrDefault("notes", "No additional notes");

        if (patientId == null || patientId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "patientId is required"));
        }

        // Get aggregated stats for this patient
        Map<String, Object> stats = readingService.getAggregatedStats(patientId, 300);
        int count = (int) stats.getOrDefault("count", 0);

        if (count == 0) {
            return ResponseEntity.ok(Map.of(
                    "report", "VERDICT: INSUFFICIENT DATA\nNo sensor readings available for analysis.\nPlease connect the monitoring device and collect readings first.\nUnable to assess fetal or uterine status.\nAction: Connect ESP32 device and begin monitoring session.",
                    "patientId", patientId
            ));
        }

        double avgDoppler = (double) stats.getOrDefault("avgDoppler", 0.0);
        double avgPiezo = (double) stats.getOrDefault("avgPiezo", 0.0);
        double dopplerStdDev = (double) stats.getOrDefault("dopplerVariability", 0.0);
        double piezoStdDev = (double) stats.getOrDefault("piezoVariability", 0.0);
        double maxDoppler = (double) stats.getOrDefault("maxDoppler", 0.0);
        double minDoppler = (double) stats.getOrDefault("minDoppler", 0.0);

        String report = geminiService.analyseSignals(
                gestationalAge, notes,
                avgDoppler, avgPiezo,
                dopplerStdDev, piezoStdDev,
                maxDoppler, minDoppler,
                count
        );

        return ResponseEntity.ok(Map.of(
                "report", report,
                "patientId", patientId,
                "readingsAnalysed", count
        ));
    }
}
