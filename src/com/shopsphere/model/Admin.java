package com.shopsphere.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Admin User with system management and analytics privileges.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String department;
    private HashSet<String> permissions;

    public Admin() {
        super();
        this.role = "ADMIN";
        this.department = "Store Operations";
        this.permissions = new HashSet<>();
        initPermissions();
    }

    public Admin(int userId, String name, String email, String passwordHash, String phone, String department) {
        super(userId, name, email, passwordHash, phone, "ADMIN");
        this.department = (department != null) ? department : "Store Operations";
        this.permissions = new HashSet<>();
        initPermissions();
    }

    private void initPermissions() {
        permissions.add("MANAGE_PRODUCTS");
        permissions.add("MANAGE_ORDERS");
        permissions.add("MANAGE_COUPONS");
        permissions.add("VIEW_ANALYTICS");
        permissions.add("SYSTEM_BACKUP");
    }

    @Override
    public void displayDashboardWelcome() {
        System.out.println("==================================================");
        System.out.println(" 🛡️  ShopSphere Admin Portal | " + name);
        System.out.println(" Department: " + department);
        System.out.println(" Permissions: " + permissions.size() + " Active Capabilities");
        System.out.println("==================================================");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
