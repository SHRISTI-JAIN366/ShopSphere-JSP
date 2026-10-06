package com.shopsphere.service;

import com.shopsphere.exception.AuthenticationException;
import com.shopsphere.model.Customer;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Customer & User Service managing accounts and authentication state.
 */
public class CustomerService {

    private final Map<String, User> userMap;

    public CustomerService() {
        this.userMap = new ConcurrentHashMap<>();
    }

    public void registerUser(User user) {
        if (user != null && user.getEmail() != null) {
            userMap.put(user.getEmail().toLowerCase().trim(), user);
        }
    }

    public Customer registerCustomer(String name, String email, String plainPassword, String phone) throws AuthenticationException {
        String cleanEmail = email != null ? email.toLowerCase().trim() : "";
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            throw new AuthenticationException("Invalid email format.");
        }
        if (userMap.containsKey(cleanEmail)) {
            throw new AuthenticationException("An account with email " + cleanEmail + " already exists.");
        }
        if (plainPassword == null || plainPassword.length() < 4) {
            throw new AuthenticationException("Password must be at least 4 characters long.");
        }

        int nextId = userMap.size() + 1;
        String hash = PasswordUtil.hashPassword(plainPassword);
        Customer customer = new Customer(nextId, name, cleanEmail, hash, phone);
        userMap.put(cleanEmail, customer);
        return customer;
    }

    public User authenticate(String email, String plainPassword) throws AuthenticationException {
        if (email == null || plainPassword == null) {
            throw new AuthenticationException("Email and password are required.");
        }
        User user = userMap.get(email.toLowerCase().trim());
        if (user == null) {
            throw new AuthenticationException("Account not found for email: " + email);
        }
        if (!PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Incorrect password. Please try again.");
        }
        return user;
    }

    public User getUserByEmail(String email) {
        if (email == null) return null;
        return userMap.get(email.toLowerCase().trim());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (User u : userMap.values()) {
            if (u instanceof Customer) {
                customers.add((Customer) u);
            }
        }
        return customers;
    }
}
