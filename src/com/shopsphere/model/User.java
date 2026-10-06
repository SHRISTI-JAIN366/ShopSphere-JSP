package com.shopsphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract Base class for Users in ShopSphere.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int userId;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String phone;
    protected String role;
    protected LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(int userId, String name, String email, String passwordHash, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email != null ? email.toLowerCase().trim() : "";
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public abstract void displayDashboardWelcome();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase().trim() : "";
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("[%s] #%d %s (%s)", role, userId, name, email);
    }
}
