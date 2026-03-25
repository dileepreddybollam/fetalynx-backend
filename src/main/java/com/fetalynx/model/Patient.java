package com.fetalynx.model;

import java.time.Instant;
import java.util.UUID;

public class Patient {
    private String id;
    private String name;
    private int age;
    private String gestationalAge;
    private String notes;
    private String createdBy;
    private Instant createdAt;

    public Patient() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }

    public Patient(String name, int age, String gestationalAge, String notes, String createdBy) {
        this();
        this.name = name;
        this.age = age;
        this.gestationalAge = gestationalAge;
        this.notes = notes;
        this.createdBy = createdBy;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGestationalAge() { return gestationalAge; }
    public void setGestationalAge(String gestationalAge) { this.gestationalAge = gestationalAge; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
