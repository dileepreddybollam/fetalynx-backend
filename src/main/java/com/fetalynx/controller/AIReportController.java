package com.fetalynx.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fetalynx.model.AIReport;
import com.fetalynx.service.AIReportService;

@RestController
public class AIReportController {

    private final AIReportService aiReportService;

    public AIReportController(AIReportService aiReportService) {
        this.aiReportService = aiReportService;
    }

    @PostMapping("/api/ai-report")
    public ResponseEntity<AIReport> generateReport(@RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        if (patientId == null || patientId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AIReport report = aiReportService.generateReport(patientId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/api/ai-reports/{patientId}")
    public ResponseEntity<List<AIReport>> getReports(@PathVariable String patientId) {
        return ResponseEntity.ok(aiReportService.getReports(patientId));
    }
}
