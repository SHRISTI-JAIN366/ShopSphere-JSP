package com.shopsphere.service;

import com.shopsphere.model.*;
import com.shopsphere.storage.CsvStorageManager;
import com.shopsphere.storage.ObjectSerializationManager;
import com.shopsphere.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Storage Service that coordinates Dual Persistence (File Serialization, CSV & MySQL).
 */
public class StorageService {

    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final CouponService couponService;

    public StorageService(ProductService productService, CustomerService customerService,
                          OrderService orderService, CouponService couponService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.couponService = couponService;
    }

    public void initializeData() {
        // 1. Try loading from serialized files first
        List<Product> savedProducts = ObjectSerializationManager.loadList("products.ser");
        List<User> savedUsers = ObjectSerializationManager.loadList("users.ser");
        List<Order> savedOrders = ObjectSerializationManager.loadList("orders.ser");

        if (!savedProducts.isEmpty()) {
            savedProducts.forEach(productService::addProduct);
        }
        if (!savedUsers.isEmpty()) {
            savedUsers.forEach(customerService::registerUser);
        }
        if (!savedOrders.isEmpty()) {
            savedOrders.forEach(orderService::addOrder);
        }

        // If no products exist, seed initial rich dataset
        if (productService.getAllProducts().isEmpty()) {
            seedDefaultData();
        }
    }

    public void seedDefaultData() {
        // 1. Categories
        Category catElectronics = new Category(1, "Electronics & Tech", "Laptops, Audio, Smart Devices");
        Category catFashion = new Category(2, "Fashion & Apparel", "Trendy jackets, activewear, footwear");
        Category catHome = new Category(3, "Home & Living", "Furniture, lighting, modern home decor");
        Category catBooks = new Category(4, "Books & Digital Courses", "Programming books, certifications, video courses");

        productService.addCategory(catElectronics);
        productService.addCategory(catFashion);
        productService.addCategory(catHome);
        productService.addCategory(catBooks);

        // 2. Physical Products
        productService.addProduct(new PhysicalProduct(
                101, 1, "MacBook Air M3 (16GB, 512GB SSD)", "Apple",
                "Apple M3 8-core CPU, 10-core GPU, 13.6-inch Liquid Retina Display, Space Grey",
                114900.0, 10.0, 20, "macbook.png", 4.9, 1.24, 0.0));

        productService.addProduct(new PhysicalProduct(
                102, 1, "Sony WH-1000XM5 Wireless Noise Cancelling Headphones", "Sony",
                "Flagship ANC over-ear headphones with 30h battery and multipoint Bluetooth",
                29990.0, 15.0, 35, "sony_headphones.png", 4.8, 0.25, 100.0));

        productService.addProduct(new PhysicalProduct(
                103, 1, "Logitech MX Master 3S Ergonomic Mouse", "Logitech",
                "8K DPI any-surface tracking, quiet clicks, ergonomic thumb wheel",
                8995.0, 5.0, 50, "mx_master.png", 4.7, 0.14, 50.0));

        productService.addProduct(new PhysicalProduct(
                104, 2, "Levi's Vintage Sherpa Trucker Denim Jacket", "Levi's",
                "100% heavy cotton denim with warm faux-shearling lining",
                5499.0, 20.0, 45, "denim_jacket.png", 4.6, 0.85, 80.0));

        productService.addProduct(new PhysicalProduct(
                105, 2, "Nike Air Zoom Pegasus 40 Road Running Shoes", "Nike",
                "Dual Zoom Air units, engineered mesh, responsive lightweight bounce",
                10495.0, 12.0, 30, "nike_shoes.png", 4.8, 0.60, 0.0));

        productService.addProduct(new PhysicalProduct(
                106, 3, "IKEA Scandinavian Solid Oak Coffee Table", "IKEA",
                "Minimalist Nordic design with solid oak veneer and powder coated steel frame",
                12999.0, 10.0, 15, "oak_table.png", 4.5, 12.50, 350.0));

        productService.addProduct(new PhysicalProduct(
                107, 4, "Effective Java (3rd Edition) by Joshua Bloch", "Addison-Wesley",
                "Essential best practices guide for the Java platform and modern APIs",
                3200.0, 25.0, 50, "effective_java.png", 4.95, 0.70, 50.0));

        // 3. Digital Products
        productService.addProduct(new DigitalProduct(
                108, 4, "Full-Stack Java Enterprise Mastery (Course + eBook)", "ShopSphere Academy",
                "Complete guide covering Jakarta Servlets, JSP, JDBC, Spring & Cloud Architecture",
                1999.0, 40.0, 99999, "java_course.png", 4.9,
                "https://downloads.shopsphere.com/courses/java-enterprise-mastery.zip", 450.0));

        productService.addProduct(new DigitalProduct(
                109, 4, "Clean Architecture & Design Patterns in Java", "TechPress",
                "50+ real-world refactoring recipes, SOLID design principles, and UML diagrams",
                799.0, 20.0, 99999, "clean_architecture.png", 4.85,
                "https://downloads.shopsphere.com/books/clean-architecture.pdf", 45.0));

        // 4. Default Coupons
        couponService.addCoupon(new Coupon(1, "WELCOME10", 10.0, 1000.0, 2000.0, LocalDate.now().plusMonths(6), true));
        couponService.addCoupon(new Coupon(2, "FESTIVE25", 25.0, 5000.0, 10000.0, LocalDate.now().plusMonths(3), true));
        couponService.addCoupon(new Coupon(3, "FLAT500", 15.0, 500.0, 1500.0, LocalDate.now().plusMonths(12), true));

        // 5. Seed Users (Admin & Customer)
        Admin admin = new Admin(1, "System Administrator", "admin@shopsphere.com",
                PasswordUtil.hashPassword("admin123"), "9876543210", "Executive Management");
        customerService.registerUser(admin);

        Customer customer = new Customer(2, "Rahul Sharma", "rahul@example.com",
                PasswordUtil.hashPassword("user123"), "9123456780");
        customer.addAddress(new Address(1, 2, "Flat 402, Green Valley Apartments, MG Road", "Bengaluru", "Karnataka", "560001", "India"));
        customerService.registerUser(customer);

        saveAllData();
    }

    public void saveAllData() {
        // Save to Binary Serialization files
        ObjectSerializationManager.saveList(productService.getAllProducts(), "products.ser");
        ObjectSerializationManager.saveList(customerService.getAllUsers(), "users.ser");
        ObjectSerializationManager.saveList(orderService.getAllOrders(), "orders.ser");

        // Save to CSV
        CsvStorageManager.exportProductsToCsv(productService.getAllProducts());
    }
}
