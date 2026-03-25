package com.fetalynx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fetalynx.model.Patient;

@Service
public class PatientService {

    private final Map<String, Patient> patients = new ConcurrentHashMap<>();

    public Patient create(String name, int age, String gestationalAge, String notes, String createdBy) {
        Patient patient = new Patient(name, age, gestationalAge, notes, createdBy);
        patients.put(patient.getId(), patient);
        return patient;
    }

    public Optional<Patient> findById(String id) {
        return Optional.ofNullable(patients.get(id));
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }

    public List<Patient> findByCreator(String email) {
        return patients.values().stream()
                .filter(p -> p.getCreatedBy().equals(email))
                .collect(Collectors.toList());
    }

    public Patient update(String id, String name, int age, String gestationalAge, String notes) {
        Patient patient = patients.get(id);
        if (patient == null) {
            throw new RuntimeException("Patient not found: " + id);
        }
        patient.setName(name);
        patient.setAge(age);
        patient.setGestationalAge(gestationalAge);
        patient.setNotes(notes);
        return patient;
    }

    public void delete(String id) {
        patients.remove(id);
    }
}
