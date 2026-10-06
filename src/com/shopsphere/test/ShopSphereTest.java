package com.shopsphere.test;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.exception.InvalidCouponException;
import com.shopsphere.model.*;
import com.shopsphere.service.*;
import com.shopsphere.storage.CsvStorageManager;
import com.shopsphere.storage.ObjectSerializationManager;
import com.shopsphere.util.PasswordUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Automated Verification & Unit Test Suite for ShopSphere.
 * Tests Models, Polymorphism, Collections, Exception Handling, Business Services, and File Persistence.
 */
public class ShopSphereTest {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("            🧪 RUNNING SHOPSPHERE AUTOMATED TEST SUITE                   ");
        System.out.println("==========================================================================");

        testPasswordHashing();
        testProductPolymorphismAndDiscounts();
        testInventoryStockOperationsAndExceptions();
        testCartOperations();
        testCouponValidationAndExceptions();
        testOrderPlacementAndStockDeduction();
        testFileSerializationPersistence();
        testCsvExportAndImport();

        System.out.println("\n==========================================================================");
        System.out.printf(" 🏁 TEST RESULTS SUMMARY: %d / %d PASSED (%.1f%%)\n",
                passedTests, totalTests, ((double) passedTests / totalTests) * 100.0);
        System.out.println("==========================================================================");

        if (passedTests == totalTests) {
            System.out.println(" ✨ ALL TESTS PASSED SUCCESSFULLY! The project is 100% verified and healthy.");
        } else {
            System.err.println(" ❌ Some tests failed. Please review errors above.");
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    private static void testPasswordHashing() {
        System.out.println("\n--- 1. Testing PasswordUtil (SHA-256 Hashing) ---");
        String plain = "mySecret123";
        String hash1 = PasswordUtil.hashPassword(plain);
        String hash2 = PasswordUtil.hashPassword(plain);

        assertTrue("Password hash must be deterministic (same input produces same hash)", hash1.equals(hash2));
        assertTrue("Hash length should be 64 hexadecimal characters", hash1.length() == 64);
        assertTrue("Password verification must succeed for matching plain password", PasswordUtil.verifyPassword(plain, hash1));
        assertTrue("Password verification must fail for wrong password", !PasswordUtil.verifyPassword("wrongPassword", hash1));
    }

    private static void testProductPolymorphismAndDiscounts() {
        System.out.println("\n--- 2. Testing Product Polymorphism & Discount Calculations ---");
        Product physical = new PhysicalProduct(1, 1, "Laptop", "BrandX", "Fast laptop", 100000.0, 10.0, 5, "laptop.png", 4.8, 2.0, 0.0);
        Product digital = new DigitalProduct(2, 4, "Course", "Academy", "Java Course", 2000.0, 50.0, 100, "course.png", 5.0, "http://dl.com", 100.0);

        assertTrue("Physical Product type check", "PHYSICAL".equals(physical.getProductType()));
        assertTrue("Digital Product type check", "DIGITAL".equals(digital.getProductType()));
        assertTrue("Physical discounted price (₹100,000 - 10% = ₹90,000)", physical.getDiscountedPrice() == 90000.0);
        assertTrue("Digital discounted price (₹2,000 - 50% = ₹1,000)", digital.getDiscountedPrice() == 1000.0);
        assertTrue("Digital delivery cost must always be FREE (₹0.0)", digital.calculateDeliveryCost() == 0.0);
    }

    private static void testInventoryStockOperationsAndExceptions() {
        System.out.println("\n--- 3. Testing Inventory Stock Operations & Custom Exceptions ---");
        Product product = new PhysicalProduct(3, 1, "Mouse", "Logitech", "Wireless mouse", 1000.0, 0.0, 10, "m.png", 4.5, 0.2, 50.0);

        assertTrue("Stock check for valid quantity (5 units)", product.hasStock(5));
        assertTrue("Stock check for excess quantity (15 units) should be false", !product.hasStock(15));

        try {
            product.deductStock(4);
            assertTrue("Deduct stock by 4 (Remaining stock should be 6)", product.getStock() == 6);
        } catch (InsufficientStockException e) {
            assertTrue("Stock deduction failed unexpectedly", false);
        }

        boolean caughtException = false;
        try {
            product.deductStock(10); // Only 6 available
        } catch (InsufficientStockException e) {
            caughtException = true;
        }
        assertTrue("Must throw InsufficientStockException when requesting more than available stock", caughtException);
    }

    private static void testCartOperations() {
        System.out.println("\n--- 4. Testing Customer Shopping Cart Operations ---");
        Customer customer = new Customer(1, "Test User", "test@user.com", "hash", "1234567890");
        Product p1 = new PhysicalProduct(1, 1, "Headphones", "Sony", "ANC", 10000.0, 10.0, 20, "h.png", 4.8, 0.5, 50.0);
        Product p2 = new DigitalProduct(2, 4, "eBook", "Author", "Guide", 500.0, 0.0, 100, "b.png", 4.9, "http://dl", 10.0);

        customer.addToCart(p1, 2); // 2 * 9000 = 18000
        customer.addToCart(p2, 1); // 1 * 500 = 500

        assertTrue("Cart total item count should be 3", customer.getCartItemCount() == 3);
        double total = customer.getCart().stream().mapToDouble(OrderItem::getSubtotal).sum();
        assertTrue("Cart subtotal calculation (₹18,000 + ₹500 = ₹18,500)", total == 18500.0);

        customer.removeFromCart(2);
        assertTrue("Cart count after removing eBook should be 2", customer.getCartItemCount() == 2);
    }

    private static void testCouponValidationAndExceptions() {
        System.out.println("\n--- 5. Testing Coupon Service & Validation Exceptions ---");
        CouponService couponService = new CouponService();
        couponService.addCoupon(new Coupon(1, "SAVE20", 20.0, 2000.0, 5000.0, LocalDate.now().plusDays(30), true));
        couponService.addCoupon(new Coupon(2, "EXPIRED10", 10.0, 500.0, 1000.0, LocalDate.now().minusDays(1), true));

        try {
            double discount = couponService.validateAndCalculateDiscount("SAVE20", 6000.0); // 20% of 6000 = 1200
            assertTrue("Valid coupon discount calculation (20% of ₹6,000 = ₹1,200)", discount == 1200.0);
        } catch (InvalidCouponException e) {
            assertTrue("Valid coupon failed unexpectedly: " + e.getMessage(), false);
        }

        // Test minimum order not met exception
        boolean caughtMinOrder = false;
        try {
            couponService.validateAndCalculateDiscount("SAVE20", 2000.0); // Min order is 5000
        } catch (InvalidCouponException e) {
            caughtMinOrder = true;
        }
        assertTrue("Must throw InvalidCouponException when order is below minimum threshold", caughtMinOrder);

        // Test expired coupon exception
        boolean caughtExpired = false;
        try {
            couponService.validateAndCalculateDiscount("EXPIRED10", 2000.0);
        } catch (InvalidCouponException e) {
            caughtExpired = true;
        }
        assertTrue("Must throw InvalidCouponException for expired coupons", caughtExpired);
    }

    private static void testOrderPlacementAndStockDeduction() {
        System.out.println("\n--- 6. Testing End-to-End Order Placement & Stock Deductions ---");
        ProductService productService = new ProductService();
        CouponService couponService = new CouponService();
        OrderService orderService = new OrderService(productService, couponService);

        Product p = new PhysicalProduct(10, 1, "Smartwatch", "Brand", "Watch", 5000.0, 0.0, 15, "w.png", 4.7, 0.2, 0.0);
        productService.addProduct(p);
        couponService.addCoupon(new Coupon(1, "DISCOUNT500", 10.0, 500.0, 2000.0, LocalDate.now().plusMonths(1), true));

        Customer customer = new Customer(1, "Alice", "alice@example.com", "hash", "9998887776");
        List<OrderItem> cart = new ArrayList<>();
        cart.add(new OrderItem(p, 3)); // 3 * 5000 = 15,000

        try {
            Order order = orderService.placeOrder(customer, cart, "123 Park Avenue, City", "DISCOUNT500", "UPI");
            assertTrue("Order ID should be assigned", order.getOrderId() > 0);
            assertTrue("Gross total should be ₹15,000", order.getGrossTotal() == 15000.0);
            assertTrue("Coupon discount should be ₹500 (capped max discount)", order.getDiscountAmount() == 500.0);
            assertTrue("Net payable should be ₹14,500", order.getNetPayable() == 14500.0);
            assertTrue("Product stock must be automatically deducted from 15 to 12", p.getStock() == 12);
            assertTrue("Customer earned loyalty points", customer.getLoyaltyPoints() > 50);
        } catch (Exception e) {
            assertTrue("Order placement failed: " + e.getMessage(), false);
        }
    }

    private static void testFileSerializationPersistence() {
        System.out.println("\n--- 7. Testing Java Object Serialization File Persistence ---");
        List<Product> products = new ArrayList<>();
        products.add(new PhysicalProduct(1, 1, "SerItem1", "B", "D", 100.0, 0.0, 10, "img", 4.0, 1.0, 0.0));
        products.add(new DigitalProduct(2, 4, "SerItem2", "B", "D", 200.0, 0.0, 20, "img", 4.5, "url", 10.0));

        boolean saved = ObjectSerializationManager.saveList(products, "test_products.ser");
        assertTrue("Save list to serialized file", saved);

        List<Product> loaded = ObjectSerializationManager.loadList("test_products.ser");
        assertTrue("Load list from serialized file should return 2 items", loaded.size() == 2);
        assertTrue("Loaded item 1 name match", "SerItem1".equals(loaded.get(0).getName()));
    }

    private static void testCsvExportAndImport() {
        System.out.println("\n--- 8. Testing CSV File Export & Import (BufferedReader/BufferedWriter) ---");
        List<Product> products = new ArrayList<>();
        products.add(new PhysicalProduct(101, 1, "CSV Physical Product", "BrandA", "Desc", 4500.0, 10.0, 25, "img.png", 4.8, 1.5, 100.0));
        products.add(new DigitalProduct(102, 4, "CSV Digital Course", "BrandB", "Desc", 1200.0, 20.0, 999, "img.png", 4.9, "https://url.com", 50.0));

        CsvStorageManager.exportProductsToCsv(products);
        List<Product> imported = CsvStorageManager.importProductsFromCsv();

        assertTrue("CSV Imported products count should be at least 2", imported.size() >= 2);
        assertTrue("First product name matches exported name", imported.get(0).getName().equals("CSV Physical Product"));
    }
}
