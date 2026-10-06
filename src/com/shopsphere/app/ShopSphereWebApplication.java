package com.shopsphere.app;

import com.shopsphere.service.*;
import com.shopsphere.web.ShopSphereWebServer;

/**
 * ShopSphere Web Server Entry Point.
 * Starts the HTTP Web Server on http://localhost:8080.
 */
public class ShopSphereWebApplication {

    public static void main(String[] args) {
        try {
            // 1. Initialize Services
            ProductService productService = new ProductService();
            CouponService couponService = new CouponService();
            CustomerService customerService = new CustomerService();
            OrderService orderService = new OrderService(productService, couponService);

            // 2. Load / Seed Data
            StorageService storageService = new StorageService(productService, customerService, orderService, couponService);
            storageService.initializeData();

            // 3. Start Web Server
            ShopSphereWebServer webServer = new ShopSphereWebServer(productService, customerService, orderService, couponService, storageService);
            webServer.start();
        } catch (Exception e) {
            System.err.println("Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
