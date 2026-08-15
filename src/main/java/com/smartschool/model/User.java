package com.smartschool.model;

import java.time.LocalDateTime;

/**
 * Core user entity - maps to the `users` table.
 * Holds authentication/authorization data only.
 * Profile data lives in role-specific tables (students, teachers, etc.)
 */
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean active;
    private int failedAttempts;
    private LocalDateTime lockedUntil;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public User(Long id, String username, String email, String passwordHash, Role role,
                boolean active, int failedAttempts, LocalDateTime lockedUntil,
                LocalDateTime lastLogin, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    // ---- Getters & Setters ----
    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }
    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }
    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }
    public String getPasswordHash()         { return passwordHash; }
    public void setPasswordHash(String ph)  { this.passwordHash = ph; }
    public Role getRole()                   { return role; }
    public void setRole(Role role)          { this.role = role; }
    public boolean isActive()               { return active; }
    public void setActive(boolean a)        { this.active = a; }
    public int getFailedAttempts()          { return failedAttempts; }
    public void setFailedAttempts(int f)    { this.failedAttempts = f; }
    public LocalDateTime getLockedUntil()   { return lockedUntil; }
    public void setLockedUntil(LocalDateTime l) { this.lockedUntil = l; }
    public LocalDateTime getLastLogin()     { return lastLogin; }
    public void setLastLogin(LocalDateTime l) { this.lastLogin = l; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u) { this.updatedAt = u; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}
