package com.fetalynx.model;

import java.time.Instant;
import java.util.UUID;

public class User {
    private String id;
    private String email;
    private String password;
    private String role;
    private Instant createdAt;

    public User() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }

    public User(String email, String password, String role) {
        this();
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
