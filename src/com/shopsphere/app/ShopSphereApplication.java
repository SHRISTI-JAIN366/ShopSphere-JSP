package com.shopsphere.app;

import com.shopsphere.menu.ConsoleMenu;
import com.shopsphere.service.*;

/**
 * ShopSphere Application Entry Point.
 * Initializes core services, loads persistent storage, and runs the interactive menu.
 */
public class ShopSphereApplication {

    public static void main(String[] args) {
        System.out.println("--------------------------------------------------");
        System.out.println(" Starting ShopSphere Enterprise Application...");
        System.out.println(" Java Version: " + System.getProperty("java.version"));
        System.out.println("--------------------------------------------------");

        // 1. Initialize Business Services
        ProductService productService = new ProductService();
        CouponService couponService = new CouponService();
        CustomerService customerService = new CustomerService();
        OrderService orderService = new OrderService(productService, couponService);

        // 2. Initialize Storage Service (Dual File & Database Persistence)
        StorageService storageService = new StorageService(productService, customerService, orderService, couponService);
        storageService.initializeData();

        // 3. Launch Interactive Console Menu
        ConsoleMenu menu = new ConsoleMenu(productService, customerService, orderService, couponService, storageService);
        menu.start();
    }
}
