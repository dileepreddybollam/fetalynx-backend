package com.fetalynx.model;

import java.util.UUID;

public class Reading {
    private String id;
    private String patientId;
    private double doppler;
    private double piezo;
    private long timestamp;

    public Reading() {
        this.id = UUID.randomUUID().toString();
    }

    public Reading(String patientId, double doppler, double piezo, long timestamp) {
        this();
        this.patientId = patientId;
        this.doppler = doppler;
        this.piezo = piezo;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public double getDoppler() { return doppler; }
    public void setDoppler(double doppler) { this.doppler = doppler; }
    public double getPiezo() { return piezo; }
    public void setPiezo(double piezo) { this.piezo = piezo; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
