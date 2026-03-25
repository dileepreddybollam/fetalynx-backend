package com.fetalynx.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fetalynx.model.Reading;
import com.fetalynx.model.dto.IngestRequest;
import com.fetalynx.service.ReadingService;

@RestController
public class IngestController {

    private final ReadingService readingService;

    public IngestController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @PostMapping("/api/ingest")
    public ResponseEntity<?> ingest(@RequestBody IngestRequest request) {
        if (request.getPatientId() == null || request.getPatientId().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "patientId is required"));
        }

        long timestamp = request.getTimestamp() > 0 ? request.getTimestamp() : System.currentTimeMillis();
        Reading reading = readingService.addReading(
                request.getPatientId(),
                request.getDoppler(),
                request.getPiezo(),
                timestamp
        );

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "id", reading.getId(),
                "total", readingService.getReadingCount(request.getPatientId())
        ));
    }

    // Compatibility endpoint for the reference ESP32 format
    @PostMapping("/api/readings")
    public ResponseEntity<?> ingestLegacy(@RequestBody Map<String, Object> body) {
        String patientId = (String) body.getOrDefault("patientId", "default");
        double doppler = body.containsKey("heartRate")
                ? ((Number) body.get("heartRate")).doubleValue()
                : ((Number) body.getOrDefault("doppler", 0)).doubleValue();
        double piezo = body.containsKey("contraction")
                ? ((Number) body.get("contraction")).doubleValue()
                : ((Number) body.getOrDefault("piezo", 0)).doubleValue();

        Reading reading = readingService.addReading(patientId, doppler, piezo, System.currentTimeMillis());

        return ResponseEntity.ok(Map.of("status", "ok", "id", reading.getId()));
    }
}
