package com.fetalynx.model.dto;

public class IngestRequest {
    private String patientId;
    private double doppler;
    private double piezo;
    private long timestamp;

    public IngestRequest() {}

    public IngestRequest(String patientId, double doppler, double piezo, long timestamp) {
        this.patientId = patientId;
        this.doppler = doppler;
        this.piezo = piezo;
        this.timestamp = timestamp;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public double getDoppler() { return doppler; }
    public void setDoppler(double doppler) { this.doppler = doppler; }
    public double getPiezo() { return piezo; }
    public void setPiezo(double piezo) { this.piezo = piezo; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
