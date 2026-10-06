package com.shopsphere.menu;

import com.shopsphere.exception.*;
import com.shopsphere.model.*;
import com.shopsphere.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console Menu System for ShopSphere.
 * Provides Customer Storefront, Shopping Cart, Checkout, Admin Control Center, and Catalog Browsing.
 */
public class ConsoleMenu {

    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final StorageService storageService;
    private final Scanner scanner;

    private User currentUser;

    public ConsoleMenu(ProductService productService, CustomerService customerService,
                       OrderService orderService, CouponService couponService,
                       StorageService storageService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.couponService = couponService;
        this.storageService = storageService;
        this.scanner = new Scanner(System.in);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void start() {
        while (true) {
            printMainMenuHeader();
            System.out.println(" 1. 🛍️  Browse Product Catalog (Search / Filter / Sort)");
            System.out.println(" 2. 🔑  Customer Login / Register");
            System.out.println(" 3. 🛡️  Admin Portal Login");
            System.out.println(" 4. 🎟️  View Available Discount Coupons");
            System.out.println(" 5. 🔄  Reset / Re-seed Demo Catalog Data");
            System.out.println(" 0. 🚪  Exit Application");
            System.out.print("\n 👉 Enter your choice (0-5): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    browseCatalogMenu(null);
                    break;
                case "2":
                    handleCustomerAuth();
                    break;
                case "3":
                    handleAdminLogin();
                    break;
                case "4":
                    displayCoupons();
                    break;
                case "5":
                    storageService.seedDefaultData();
                    System.out.println("\n ✅ Demo data re-seeded successfully!");
                    pause();
                    break;
                case "0":
                    System.out.println("\n 💾 Saving all system data before exit...");
                    storageService.saveAllData();
                    System.out.println(" 👋 Thank you for choosing ShopSphere! Have a great day!\n");
                    return;
                default:
                    System.out.println(" ⚠️ Invalid option! Please enter a valid number (0-5).");
                    pause();
            }
        }
    }

    private void printMainMenuHeader() {
        System.out.println("\n==========================================================================");
        System.out.println("              🌟 SHOPSPHERE ADVANCED E-COMMERCE SYSTEM 🌟                 ");
        System.out.println("           Java Enterprise Capstone Project | RTU Java Syllabus          ");
        System.out.println("==========================================================================");
    }

    // =========================================================================
    // AUTHENTICATION
    // =========================================================================

    private void handleCustomerAuth() {
        System.out.println("\n--- CUSTOMER ACCESS ---");
        System.out.println(" 1. Existing Customer Login");
        System.out.println(" 2. Create New Account (Register)");
        System.out.println(" 0. Back to Main Menu");
        System.out.print(" 👉 Choice: ");

        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            loginCustomer();
        } else if ("2".equals(choice)) {
            registerNewCustomer();
        }
    }

    private void loginCustomer() {
        System.out.print(" 📧 Enter Email (e.g. rahul@example.com): ");
        String email = scanner.nextLine().trim();
        System.out.print(" 🔑 Enter Password (e.g. user123): ");
        String password = scanner.nextLine().trim();

        try {
            User user = customerService.authenticate(email, password);
            if (user instanceof Customer) {
                this.currentUser = user;
                System.out.println("\n ✅ Login Successful! Welcome back, " + user.getName() + "!");
                customerDashboard((Customer) user);
            } else {
                System.out.println(" ⚠️ This account is an Administrator. Please use Admin Portal (Option 3).");
                pause();
            }
        } catch (AuthenticationException e) {
            System.out.println(" ❌ " + e.getMessage());
            pause();
        }
    }

    private void registerNewCustomer() {
        System.out.println("\n--- NEW CUSTOMER REGISTRATION ---");
        System.out.print(" 👤 Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print(" 📧 Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print(" 🔑 Password (min 4 chars): ");
        String password = scanner.nextLine().trim();
        System.out.print(" 📱 Mobile Number: ");
        String phone = scanner.nextLine().trim();

        try {
            Customer customer = customerService.registerCustomer(name, email, password, phone);
            System.out.println(" 🏠 Enter Delivery Address:");
            System.out.print("   Street / Flat: ");
            String street = scanner.nextLine().trim();
            System.out.print("   City: ");
            String city = scanner.nextLine().trim();
            System.out.print("   State: ");
            String state = scanner.nextLine().trim();
            System.out.print("   Pincode: ");
            String pincode = scanner.nextLine().trim();

            customer.addAddress(new Address(1, customer.getUserId(), street, city, state, pincode, "India"));
            storageService.saveAllData();

            System.out.println("\n ✅ Registration Successful! You received 50 bonus loyalty points!");
            this.currentUser = customer;
            customerDashboard(customer);
        } catch (AuthenticationException e) {
            System.out.println(" ❌ Registration failed: " + e.getMessage());
            pause();
        }
    }

    private void handleAdminLogin() {
        System.out.println("\n--- 🛡️ ADMIN PORTAL AUTHENTICATION ---");
        System.out.print(" 📧 Admin Email (e.g. admin@shopsphere.com): ");
        String email = scanner.nextLine().trim();
        System.out.print(" 🔑 Admin Password (e.g. admin123): ");
        String password = scanner.nextLine().trim();

        try {
            User user = customerService.authenticate(email, password);
            if (user instanceof Admin) {
                this.currentUser = user;
                System.out.println("\n ✅ Welcome Administrator " + user.getName() + "!");
                adminDashboard((Admin) user);
            } else {
                System.out.println(" ❌ Access Denied: This user does not have Administrator privileges.");
                pause();
            }
        } catch (AuthenticationException e) {
            System.out.println(" ❌ " + e.getMessage());
            pause();
        }
    }

    // =========================================================================
    // CUSTOMER DASHBOARD & STOREFRONT
    // =========================================================================

    private void customerDashboard(Customer customer) {
        while (true) {
            customer.displayDashboardWelcome();
            System.out.println(" 1. 🛍️  Browse Catalog & Add Items to Cart");
            System.out.println(" 2. 🛒  View Shopping Cart (" + customer.getCartItemCount() + " items)");
            System.out.println(" 3. 💳  Proceed to Checkout");
            System.out.println(" 4. 📦  My Order History & Invoices");
            System.out.println(" 5. 👤  My Profile & Saved Addresses");
            System.out.println(" 0. 🚪  Logout");
            System.out.print("\n 👉 Select option (0-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    browseCatalogMenu(customer);
                    break;
                case "2":
                    viewCartMenu(customer);
                    break;
                case "3":
                    checkoutFlow(customer);
                    break;
                case "4":
                    viewOrderHistory(customer);
                    break;
                case "5":
                    viewCustomerProfile(customer);
                    break;
                case "0":
                    System.out.println(" 👋 Logged out successfully.");
                    this.currentUser = null;
                    return;
                default:
                    System.out.println(" ⚠️ Invalid option.");
            }
        }
    }

    private void browseCatalogMenu(Customer customer) {
        while (true) {
            System.out.println("\n--- 🔍 PRODUCT CATALOG & SEARCH ---");
            System.out.println(" 1. View All Products");
            System.out.println(" 2. Search Products by Keyword / Brand");
            System.out.println(" 3. Filter Products by Category");
            System.out.println(" 4. Sort Products by Price (Low to High)");
            System.out.println(" 5. Sort Products by Price (High to Low)");
            System.out.println(" 6. View Top Rated Products (★)");
            if (customer != null) {
                System.out.println(" 7. ➕ Add Product to Cart by ID");
            }
            System.out.println(" 0. Back");
            System.out.print(" 👉 Option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    renderProductTable(productService.getAllProducts());
                    break;
                case "2":
                    System.out.print(" 🔍 Enter search term: ");
                    String query = scanner.nextLine().trim();
                    renderProductTable(productService.searchProducts(query));
                    break;
                case "3":
                    filterByCategoryMenu();
                    break;
                case "4":
                    renderProductTable(productService.getProductsSortedByPrice(true));
                    break;
                case "5":
                    renderProductTable(productService.getProductsSortedByPrice(false));
                    break;
                case "6":
                    renderProductTable(productService.getProductsSortedByRating());
                    break;
                case "7":
                    if (customer != null) {
                        addProductToCart(customer);
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println(" ⚠️ Invalid option.");
            }
        }
    }

    private void filterByCategoryMenu() {
        List<Category> categories = productService.getAllCategories();
        System.out.println("\nSelect Category to Filter:");
        for (Category c : categories) {
            System.out.println(" [" + c.getCategoryId() + "] " + c.getName());
        }
        System.out.print(" 👉 Enter Category ID: ");
        try {
            int catId = Integer.parseInt(scanner.nextLine().trim());
            renderProductTable(productService.filterByCategory(catId));
        } catch (NumberFormatException e) {
            System.out.println(" ⚠️ Please enter a numeric Category ID.");
        }
    }

    private void renderProductTable(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("\n ⚠️ No matching products found.");
            pause();
            return;
        }

        System.out.println("\n==========================================================================================================");
        System.out.printf(" %-5s %-38s %-12s %-12s %-8s %-12s %-8s %-6s\n",
                "ID", "Product Name", "Brand", "Original", "OFF", "Final Price", "Stock", "Rating");
        System.out.println("==========================================================================================================");

        for (Product p : products) {
            String name = p.getName().length() > 36 ? p.getName().substring(0, 33) + "..." : p.getName();
            String brand = p.getBrand() != null ? (p.getBrand().length() > 10 ? p.getBrand().substring(0, 8) + ".." : p.getBrand()) : "N/A";
            System.out.printf(" %-5d %-38s %-12s ₹%-11.2f %3.0f%%   ₹%-11.2f %-8d ★ %.1f\n",
                    p.getProductId(), name, brand, p.getPrice(), p.getDiscountPercent(),
                    p.getDiscountedPrice(), p.getStock(), p.getRating());
        }
        System.out.println("==========================================================================================================");
        pause();
    }

    private void addProductToCart(Customer customer) {
        System.out.print(" 📦 Enter Product ID to add: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Product product = productService.getProductById(id);

            System.out.print(" 🔢 Enter Quantity: ");
            int qty = Integer.parseInt(scanner.nextLine().trim());
            if (qty <= 0) {
                System.out.println(" ⚠️ Quantity must be greater than 0.");
                return;
            }

            if (!product.hasStock(qty)) {
                System.out.println(String.format(" ❌ Insufficient stock! Only %d units available.", product.getStock()));
                return;
            }

            customer.addToCart(product, qty);
            System.out.println(String.format(" ✅ Added %d x '%s' to your cart!", qty, product.getName()));
        } catch (ProductNotFoundException e) {
            System.out.println(" ❌ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(" ⚠️ Please enter a valid number.");
        }
        pause();
    }

    private void viewCartMenu(Customer customer) {
        List<OrderItem> cart = customer.getCart();
        if (cart.isEmpty()) {
            System.out.println("\n 🛒 Your shopping cart is empty!");
            pause();
            return;
        }

        System.out.println("\n==========================================================================");
        System.out.println("                       🛍️ YOUR SHOPPING CART                              ");
        System.out.println("==========================================================================");
        System.out.printf(" %-4s %-38s %-6s %-10s %-10s\n", "#", "Item Name", "Qty", "Price", "Subtotal");
        System.out.println("--------------------------------------------------------------------------");

        int index = 1;
        double total = 0.0;
        double savings = 0.0;

        for (OrderItem item : cart) {
            String name = item.getProduct().getName();
            if (name.length() > 36) name = name.substring(0, 33) + "...";
            System.out.printf(" %-4d %-38s %-6d ₹%-9.2f ₹%-9.2f\n",
                    index++, name, item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
            total += item.getSubtotal();
            savings += item.getSavings();
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" Subtotal:                                             ₹%10.2f\n", total);
        if (savings > 0) {
            System.out.printf(" Total Savings on MRP:                                 ₹%10.2f\n", savings);
        }
        System.out.println("==========================================================================");

        System.out.println("\n 1. 💳 Proceed to Checkout");
        System.out.println(" 2. ❌ Remove Item from Cart");
        System.out.println(" 3. 🗑️ Clear Entire Cart");
        System.out.println(" 0. Back to Customer Menu");
        System.out.print(" 👉 Choice: ");

        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            checkoutFlow(customer);
        } else if ("2".equals(choice)) {
            System.out.print(" Enter Product ID to remove: ");
            try {
                int pid = Integer.parseInt(scanner.nextLine().trim());
                customer.removeFromCart(pid);
                System.out.println(" ✅ Item removed.");
            } catch (Exception e) {
                System.out.println(" ⚠️ Invalid ID.");
            }
            pause();
        } else if ("3".equals(choice)) {
            customer.clearCart();
            System.out.println(" ✅ Cart cleared.");
            pause();
        }
    }

    private void checkoutFlow(Customer customer) {
        if (customer.getCart().isEmpty()) {
            System.out.println("\n ⚠️ Your cart is empty. Please add products first.");
            pause();
            return;
        }

        System.out.println("\n==========================================================================");
        System.out.println("                        💳 SECURE CHECKOUT WIZARD                         ");
        System.out.println("==========================================================================");

        // 1. Shipping Address Selection
        String shippingAddress;
        if (!customer.getAddresses().isEmpty()) {
            Address addr = customer.getAddresses().get(0);
            System.out.println(" 📍 Deliver to: " + addr.getFullFormattedAddress());
            System.out.print("   Use this address? (Y/N, default Y): ");
            String useDefault = scanner.nextLine().trim();
            if ("N".equalsIgnoreCase(useDefault)) {
                System.out.print("   Enter new delivery street/city/pincode: ");
                shippingAddress = scanner.nextLine().trim();
            } else {
                shippingAddress = addr.getFullFormattedAddress();
            }
        } else {
            System.out.print(" 📍 Enter Delivery Address: ");
            shippingAddress = scanner.nextLine().trim();
        }

        // 2. Promo / Coupon Code
        System.out.print("\n 🎟️ Have a discount coupon code? (e.g. WELCOME10, FLAT500, or press Enter to skip): ");
        String couponCode = scanner.nextLine().trim();
        if (couponCode.isEmpty()) couponCode = null;

        // 3. Payment Method
        System.out.println("\n 💰 Select Payment Method:");
        System.out.println("   1. Credit / Debit Card");
        System.out.println("   2. UPI / Net Banking (GPay / PhonePe)");
        System.out.println("   3. Cash on Delivery (COD)");
        System.out.print("   👉 Choose method (1-3): ");
        String pChoice = scanner.nextLine().trim();
        String paymentMethod = switch (pChoice) {
            case "1" -> "CREDIT_CARD";
            case "2" -> "UPI_NETBANKING";
            default -> "CASH_ON_DELIVERY";
        };

        // 4. Place Order
        try {
            Order order = orderService.placeOrder(customer, customer.getCart(), shippingAddress, couponCode, paymentMethod);
            customer.clearCart();
            storageService.saveAllData();

            System.out.println("\n 🎉 ORDER PLACED SUCCESSFULLY!");
            System.out.println(order.generateInvoiceSummary());
        } catch (InsufficientStockException | InvalidCouponException e) {
            System.out.println("\n ❌ Checkout Failed: " + e.getMessage());
        }
        pause();
    }

    private void viewOrderHistory(Customer customer) {
        List<Order> orders = orderService.getOrdersByCustomer(customer.getUserId());
        if (orders.isEmpty()) {
            System.out.println("\n 📦 You have not placed any orders yet.");
            pause();
            return;
        }

        System.out.println("\n==========================================================================");
        System.out.println("                     📜 YOUR ORDER HISTORY & INVOICES                     ");
        System.out.println("==========================================================================");
        for (Order o : orders) {
            System.out.printf(" Order #%-6d | Date: %-16s | Total: ₹%-9.2f | Status: [%s]\n",
                    o.getOrderId(), o.getOrderDate().toLocalDate(), o.getNetPayable(), o.getOrderStatus());
        }
        System.out.println("==========================================================================");

        System.out.print(" 👉 Enter Order ID to view complete invoice receipt (or 0 to back): ");
        try {
            int oid = Integer.parseInt(scanner.nextLine().trim());
            if (oid > 0) {
                Order order = orderService.getOrderById(oid);
                System.out.println(order.generateInvoiceSummary());
            }
        } catch (Exception e) {
            System.out.println(" ⚠️ " + e.getMessage());
        }
        pause();
    }

    private void viewCustomerProfile(Customer customer) {
        System.out.println("\n--- 👤 CUSTOMER PROFILE ---");
        System.out.println(" User ID:         #" + customer.getUserId());
        System.out.println(" Name:            " + customer.getName());
        System.out.println(" Email:           " + customer.getEmail());
        System.out.println(" Phone:           " + customer.getPhone());
        System.out.println(" Loyalty Points:  " + customer.getLoyaltyPoints() + " pts (Worth ₹" + (customer.getLoyaltyPoints() * 2) + ")");
        System.out.println(" Saved Addresses:");
        for (Address a : customer.getAddresses()) {
            System.out.println("  - " + a.getFullFormattedAddress());
        }
        pause();
    }

    // =========================================================================
    // ADMIN DASHBOARD
    // =========================================================================

    private void adminDashboard(Admin admin) {
        while (true) {
            admin.displayDashboardWelcome();
            System.out.println(" 1. ➕ Add New Product (Physical / Digital)");
            System.out.println(" 2. 📦 Restock / Update Product Stock");
            System.out.println(" 3. ❌ Remove Product from Catalog");
            System.out.println(" 4. 📜 View All Customer Orders & Update Delivery Status");
            System.out.println(" 5. 👥 View All Registered Users");
            System.out.println(" 6. 📊 Store Analytics & Revenue Summary");
            System.out.println(" 7. 🎟️ Create New Discount Coupon");
            System.out.println(" 8. 💾 Export Catalog to CSV & Backup Binary Data");
            System.out.println(" 0. 🚪 Logout");
            System.out.print("\n 👉 Select option (0-8): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addNewProductFlow();
                    break;
                case "2":
                    restockProductFlow();
                    break;
                case "3":
                    removeProductFlow();
                    break;
                case "4":
                    adminOrderManagement();
                    break;
                case "5":
                    viewAllUsers();
                    break;
                case "6":
                    viewAnalytics();
                    break;
                case "7":
                    createCouponFlow();
                    break;
                case "8":
                    storageService.saveAllData();
                    System.out.println("\n ✅ Catalog exported to CSV (data/products.csv) & binary state saved!");
                    pause();
                    break;
                case "0":
                    System.out.println(" 👋 Admin logged out.");
                    this.currentUser = null;
                    return;
                default:
                    System.out.println(" ⚠️ Invalid choice.");
            }
        }
    }

    private void addNewProductFlow() {
        System.out.println("\n--- ➕ ADD NEW PRODUCT ---");
        System.out.println(" Select Product Type: 1. Physical Goods | 2. Digital Download");
        System.out.print(" 👉 Type (1 or 2): ");
        String type = scanner.nextLine().trim();

        try {
            System.out.print(" Product Name: ");
            String name = scanner.nextLine().trim();
            System.out.print(" Brand / Publisher: ");
            String brand = scanner.nextLine().trim();
            System.out.print(" Description: ");
            String desc = scanner.nextLine().trim();
            System.out.print(" Price (₹): ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print(" Discount Percent (0-100%): ");
            double discount = Double.parseDouble(scanner.nextLine().trim());
            System.out.print(" Initial Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());

            int nextId = productService.getNextProductId();

            if ("2".equals(type)) {
                System.out.print(" Download URL: ");
                String url = scanner.nextLine().trim();
                System.out.print(" File Size (MB): ");
                double size = Double.parseDouble(scanner.nextLine().trim());
                productService.addProduct(new DigitalProduct(nextId, 4, name, brand, desc, price, discount, stock, "digital.png", 5.0, url, size));
            } else {
                System.out.print(" Weight (kg): ");
                double weight = Double.parseDouble(scanner.nextLine().trim());
                System.out.print(" Shipping Fee (₹): ");
                double ship = Double.parseDouble(scanner.nextLine().trim());
                productService.addProduct(new PhysicalProduct(nextId, 1, name, brand, desc, price, discount, stock, "physical.png", 5.0, weight, ship));
            }

            storageService.saveAllData();
            System.out.println("\n ✅ Product [#" + nextId + "] '" + name + "' added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("\n ❌ Invalid numerical format! Product creation cancelled.");
        }
        pause();
    }

    private void restockProductFlow() {
        System.out.print(" 📦 Enter Product ID to restock: ");
        try {
            int pid = Integer.parseInt(scanner.nextLine().trim());
            Product p = productService.getProductById(pid);
            System.out.println(" Current Stock for '" + p.getName() + "': " + p.getStock());
            System.out.print(" ➕ Additional Quantity to Add: ");
            int addQty = Integer.parseInt(scanner.nextLine().trim());

            productService.restockProduct(pid, addQty);
            storageService.saveAllData();
            System.out.println(" ✅ Product restocked! New stock level: " + p.getStock());
        } catch (Exception e) {
            System.out.println(" ❌ " + e.getMessage());
        }
        pause();
    }

    private void removeProductFlow() {
        System.out.print(" 🗑️ Enter Product ID to remove: ");
        try {
            int pid = Integer.parseInt(scanner.nextLine().trim());
            boolean removed = productService.removeProduct(pid);
            if (removed) {
                storageService.saveAllData();
                System.out.println(" ✅ Product #" + pid + " deleted from catalog.");
            } else {
                System.out.println(" ⚠️ Product not found.");
            }
        } catch (Exception e) {
            System.out.println(" ⚠️ " + e.getMessage());
        }
        pause();
    }

    private void adminOrderManagement() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("\n 📜 No customer orders placed yet.");
            pause();
            return;
        }

        System.out.println("\n==================================================================================");
        System.out.println("                         🛠️ CUSTOMER ORDER MANAGEMENT                             ");
        System.out.println("==================================================================================");
        for (Order o : orders) {
            System.out.printf(" Order #%-5d | Customer: %-18s | Net: ₹%-9.2f | Status: [%s]\n",
                    o.getOrderId(), o.getCustomerName(), o.getNetPayable(), o.getOrderStatus());
        }
        System.out.println("==================================================================================");

        System.out.print(" 👉 Enter Order ID to update status (or 0 to exit): ");
        try {
            int oid = Integer.parseInt(scanner.nextLine().trim());
            if (oid > 0) {
                System.out.println(" Select New Status: 1. PROCESSING | 2. SHIPPED | 3. DELIVERED | 4. CANCELLED");
                System.out.print(" Choice (1-4): ");
                String stChoice = scanner.nextLine().trim();
                String newStatus = switch (stChoice) {
                    case "2" -> "SHIPPED";
                    case "3" -> "DELIVERED";
                    case "4" -> "CANCELLED";
                    default -> "PROCESSING";
                };
                orderService.updateOrderStatus(oid, newStatus);
                storageService.saveAllData();
                System.out.println(" ✅ Order #" + oid + " status updated to " + newStatus);
            }
        } catch (Exception e) {
            System.out.println(" ⚠️ " + e.getMessage());
        }
        pause();
    }

    private void viewAllUsers() {
        List<User> users = customerService.getAllUsers();
        System.out.println("\n==========================================================================");
        System.out.println("                     👥 REGISTERED USER ACCOUNTS                          ");
        System.out.println("==========================================================================");
        for (User u : users) {
            System.out.printf(" #%-4d | [%-8s] | %-20s | %-25s | %s\n",
                    u.getUserId(), u.getRole(), u.getName(), u.getEmail(), u.getPhone());
        }
        System.out.println("==========================================================================");
        pause();
    }

    private void viewAnalytics() {
        List<Order> orders = orderService.getAllOrders();
        double revenue = orderService.calculateTotalRevenue();
        int totalProducts = productService.getAllProducts().size();
        int totalUsers = customerService.getAllUsers().size();

        System.out.println("\n==========================================================================");
        System.out.println("                      📊 SHOPSPHERE STORE ANALYTICS                       ");
        System.out.println("==========================================================================");
        System.out.printf(" 💰 Total Gross Store Revenue:            ₹%.2f\n", revenue);
        System.out.printf(" 📦 Total Orders Processed:               %d\n", orders.size());
        System.out.printf(" 🛍️  Active Catalog Products:             %d\n", totalProducts);
        System.out.printf(" 👥 Registered User Accounts:             %d\n", totalUsers);
        System.out.println("==========================================================================");
        pause();
    }

    private void createCouponFlow() {
        System.out.println("\n--- 🎟️ CREATE DISCOUNT COUPON ---");
        try {
            System.out.print(" Coupon Code (e.g. SUMMER30): ");
            String code = scanner.nextLine().trim().toUpperCase();
            System.out.print(" Discount Percentage (e.g. 20): ");
            double pct = Double.parseDouble(scanner.nextLine().trim());
            System.out.print(" Max Discount Amount (₹): ");
            double max = Double.parseDouble(scanner.nextLine().trim());
            System.out.print(" Min Order Amount Required (₹): ");
            double min = Double.parseDouble(scanner.nextLine().trim());

            couponService.addCoupon(new Coupon(couponService.getAllCoupons().size() + 1, code, pct, max, min, LocalDate.now().plusMonths(6), true));
            System.out.println(" ✅ Coupon '" + code + "' created successfully!");
        } catch (NumberFormatException e) {
            System.out.println("\n ❌ Invalid numerical values! Coupon creation cancelled.");
        }
        pause();
    }

    private void displayCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        System.out.println("\n==========================================================================");
        System.out.println("                     🎟️ ACTIVE DISCOUNT COUPONS                           ");
        System.out.println("==========================================================================");
        for (Coupon c : coupons) {
            System.out.println(" ✨ " + c.toString());
        }
        System.out.println("==========================================================================");
        pause();
    }

    private void pause() {
        System.out.print("\n Press [Enter] to continue...");
        scanner.nextLine();
    }
}
