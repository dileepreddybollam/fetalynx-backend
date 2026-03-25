package com.fetalynx.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fetalynx.model.User;

@Service
public class UserService {

    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password, String role) {
        if (usersByEmail.containsKey(email)) {
            throw new RuntimeException("User already exists with email: " + email);
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, role != null ? role : "doctor");
        usersById.put(user.getId(), user);
        usersByEmail.put(email, user);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    public boolean authenticate(String email, String rawPassword) {
        return findByEmail(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }
}
