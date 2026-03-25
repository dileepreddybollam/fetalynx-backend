package com.fetalynx.model.dto;

public class PatientRequest {
    private String name;
    private int age;
    private String gestationalAge;
    private String notes;

    public PatientRequest() {}

    public PatientRequest(String name, int age, String gestationalAge, String notes) {
        this.name = name;
        this.age = age;
        this.gestationalAge = gestationalAge;
        this.notes = notes;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGestationalAge() { return gestationalAge; }
    public void setGestationalAge(String gestationalAge) { this.gestationalAge = gestationalAge; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
