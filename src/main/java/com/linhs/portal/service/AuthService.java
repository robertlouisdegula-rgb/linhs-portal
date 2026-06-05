package com.linhs.portal.service;

import com.linhs.portal.model.User;
import com.linhs.portal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Added standard Spring Security Encoder

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates user login credentials against the database records.
     */
    public boolean login(String email, String password, HttpSession session) {
        if (email == null || password == null || session == null) {
            return false;
        }

        // 1. Locate user record by email
        Optional<User> userOptional = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOptional.isEmpty()) {
            return false; // User not found
        }

        User user = userOptional.get();

        // 2. IMPORTANT FIX: Use BCrypt's matches() method instead of SHA-256
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false; // Password mismatch
        }

        // 3. Register user metadata parameters into the session
        session.setAttribute("userName", user.getName());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("roleName", user.getRoleName().toUpperCase());
        
        // Map section assignment strictly for ADVISER roles
        if ("ADVISER".equalsIgnoreCase(user.getRoleName())) {
            session.setAttribute("assignedSection", user.getAssignedSection());
        } else {
            session.setAttribute("assignedSection", null);
        }

        return true;
    }

    /**
     * Authenticates a user by username/email and password, returning the User object if successful.
     */
    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        
        Optional<User> userOptional = userRepository.findByEmail(username.trim().toLowerCase());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Encodes a plain-text password with BCrypt.
     * Use this when saving passwords directly via the repository (e.g., provisioning advisers).
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Registers a new system user into the application database.
     */
    public User registerUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User details, email, and password fields cannot be empty.");
        }

        // Normalize email records
        String normalizedEmail = user.getEmail().trim().toLowerCase();

        // Check if user email record already exists in database
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return null; // Duplicate profile
        }

        // Set normalized and encrypted secure fields
        user.setEmail(normalizedEmail);
        
        // IMPORTANT FIX: Encode new user passwords with BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Ensure roles are capitalized to uniform compliance with role checks
        if (user.getRoleName() != null) {
            user.setRoleName(user.getRoleName().toUpperCase());
        }

        return userRepository.save(user);
    }
}