package com.fetalynx.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AIReport {
    private String id;
    private String patientId;
    private String summary;
    private List<String> riskIndicators;
    private List<String> recommendations;
    private Instant createdAt;

    public AIReport() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }

    public AIReport(String patientId, String summary, List<String> riskIndicators, List<String> recommendations) {
        this();
        this.patientId = patientId;
        this.summary = summary;
        this.riskIndicators = riskIndicators;
        this.recommendations = recommendations;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getRiskIndicators() { return riskIndicators; }
    public void setRiskIndicators(List<String> riskIndicators) { this.riskIndicators = riskIndicators; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
