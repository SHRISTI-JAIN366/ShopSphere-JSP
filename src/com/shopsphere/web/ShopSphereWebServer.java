package com.shopsphere.web;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.exception.InvalidCouponException;
import com.shopsphere.model.*;
import com.shopsphere.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Built-in ShopSphere Localhost Web Server (runs on http://localhost:8080).
 * Uses Java Standard Library HttpServer without requiring any external web server setup.
 */
public class ShopSphereWebServer {

    private int activePort = 8080;
    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final StorageService storageService;

    public ShopSphereWebServer(ProductService productService, CustomerService customerService,
                               OrderService orderService, CouponService couponService,
                               StorageService storageService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.couponService = couponService;
        this.storageService = storageService;
    }

    public CouponService getCouponService() {
        return couponService;
    }

    public int getActivePort() {
        return activePort;
    }

    public void start() throws IOException {
        int[] candidatePorts = {8080, 8081, 8082, 8088, 9000, 0};
        HttpServer server = null;
        for (int port : candidatePorts) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                this.activePort = server.getAddress().getPort();
                break;
            } catch (BindException e) {
                // Port busy, try next candidate
            }
        }

        if (server == null) {
            throw new IOException("Could not bind HttpServer to any available port.");
        }

        server.createContext("/", new HomeHandler());
        server.createContext("/products", new ProductsHandler());
        server.createContext("/cart", new CartHandler());
        server.createContext("/checkout", new CheckoutHandler());
        server.createContext("/admin", new AdminHandler());
        server.createContext("/api/products", new ApiProductsHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("==========================================================================");
        System.out.println(" 🌐 SHOPSPHERE WEB SERVER IS RUNNING ON LOCALHOST!                        ");
        System.out.println("==========================================================================");
        System.out.println(" 👉 Storefront URL:      http://localhost:" + activePort + "/");
        System.out.println(" 👉 Product Catalog:     http://localhost:" + activePort + "/products");
        System.out.println(" 👉 Shopping Cart:       http://localhost:" + activePort + "/cart");
        System.out.println(" 👉 Admin Dashboard:     http://localhost:" + activePort + "/admin");
        System.out.println(" 👉 REST API Endpoint:   http://localhost:" + activePort + "/api/products");
        System.out.println(" Press Ctrl+C in terminal to stop the web server.\n");
    }

    // =========================================================================
    // HTTP HANDLERS
    // =========================================================================

    private class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Product> products = productService.getAllProducts();
            String html = renderPageTemplate("ShopSphere — Modern Java E-Commerce Store", renderStorefrontHtml(products));
            sendHtmlResponse(exchange, html, 200);
        }
    }

    private class ProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            String search = queryParams.get("search");
            List<Product> products = (search != null && !search.trim().isEmpty())
                    ? productService.searchProducts(search)
                    : productService.getAllProducts();

            String content = "<div class='container py-4'>"
                    + "<div class='d-flex justify-content-between align-items-center mb-4'>"
                    + "<h2><i class='bi bi-grid-fill text-primary'></i> Explore Products (" + products.size() + ")</h2>"
                    + "<form class='d-flex' method='GET' action='/products'>"
                    + "<input class='form-control me-2' type='search' name='search' placeholder='Search iPhone, Nike, Java...' value='" + (search != null ? search : "") + "'>"
                    + "<button class='btn btn-primary' type='submit'>Search</button>"
                    + "</form></div>"
                    + renderProductCardsGrid(products) + "</div>";

            String html = renderPageTemplate("Products Catalog — ShopSphere", content);
            sendHtmlResponse(exchange, html, 200);
        }
    }

    private class CartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Customer customer = (Customer) customerService.getUserByEmail("rahul@example.com");
            if (customer == null) {
                customer = customerService.getAllCustomers().get(0);
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange);
                String action = form.get("action");
                if ("add".equalsIgnoreCase(action)) {
                    int pid = Integer.parseInt(form.get("productId"));
                    int qty = Integer.parseInt(form.getOrDefault("quantity", "1"));
                    try {
                        Product p = productService.getProductById(pid);
                        customer.addToCart(p, qty);
                    } catch (Exception ignored) {}
                } else if ("clear".equalsIgnoreCase(action)) {
                    customer.clearCart();
                } else if ("remove".equalsIgnoreCase(action)) {
                    int pid = Integer.parseInt(form.get("productId"));
                    customer.removeFromCart(pid);
                }
                // Redirect back to cart
                exchange.getResponseHeaders().set("Location", "/cart");
                exchange.sendResponseHeaders(303, -1);
                return;
            }

            String content = renderCartHtml(customer);
            String html = renderPageTemplate("Shopping Cart — ShopSphere", content);
            sendHtmlResponse(exchange, html, 200);
        }
    }

    private class CheckoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Customer customer = (Customer) customerService.getUserByEmail("rahul@example.com");
            if (customer == null) customer = customerService.getAllCustomers().get(0);

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange);
                String address = form.getOrDefault("address", "Default Shipping Address");
                String coupon = form.get("coupon");
                String method = form.getOrDefault("paymentMethod", "UPI_NETBANKING");

                try {
                    Order order = orderService.placeOrder(customer, customer.getCart(), address, coupon, method);
                    customer.clearCart();
                    storageService.saveAllData();

                    String invoiceHtml = "<div class='container py-5'>"
                            + "<div class='alert alert-success d-flex align-items-center mb-4'>"
                            + "<i class='bi bi-check-circle-fill fs-2 me-3'></i>"
                            + "<div><h4 class='alert-heading mb-1'>Order Placed Successfully!</h4>"
                            + "<p class='mb-0'>Your order <strong>#" + order.getOrderId() + "</strong> has been confirmed. A confirmation receipt has been sent to " + customer.getEmail() + ".</p></div></div>"
                            + "<div class='card shadow-sm border-0'><div class='card-body p-4'>"
                            + "<pre class='bg-light p-3 rounded border font-monospace'>" + order.generateInvoiceSummary() + "</pre>"
                            + "<div class='text-center mt-3'><a href='/' class='btn btn-primary btn-lg'><i class='bi bi-arrow-left'></i> Continue Shopping</a></div>"
                            + "</div></div></div>";

                    String html = renderPageTemplate("Order Confirmed — ShopSphere", invoiceHtml);
                    sendHtmlResponse(exchange, html, 200);
                    return;
                } catch (InsufficientStockException | InvalidCouponException e) {
                    String errHtml = "<div class='container py-5'><div class='alert alert-danger'>"
                            + "<h4>Checkout Error</h4><p>" + e.getMessage() + "</p>"
                            + "<a href='/cart' class='btn btn-outline-danger'>Back to Cart</a></div></div>";
                    sendHtmlResponse(exchange, renderPageTemplate("Checkout Error", errHtml), 400);
                    return;
                }
            }

            exchange.getResponseHeaders().set("Location", "/cart");
            exchange.sendResponseHeaders(303, -1);
        }
    }

    private class AdminHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Order> orders = orderService.getAllOrders();
            double revenue = orderService.calculateTotalRevenue();
            int totalProducts = productService.getAllProducts().size();
            int totalUsers = customerService.getAllUsers().size();

            StringBuilder sb = new StringBuilder();
            sb.append("<div class='container py-4'>");
            sb.append("<div class='d-flex justify-content-between align-items-center mb-4'>");
            sb.append("<h2><i class='bi bi-shield-lock-fill text-danger'></i> Admin Control Center</h2>");
            sb.append("<span class='badge bg-success fs-6'>System Online</span></div>");

            // KPI Cards
            sb.append("<div class='row g-4 mb-4'>");
            sb.append("<div class='col-md-3'><div class='card border-0 shadow-sm bg-primary text-white p-3 rounded-4'><h5>Total Revenue</h5><h2>₹").append(String.format("%.2f", revenue)).append("</h2><small>Live processed gross</small></div></div>");
            sb.append("<div class='col-md-3'><div class='card border-0 shadow-sm bg-dark text-white p-3 rounded-4'><h5>Total Orders</h5><h2>").append(orders.size()).append("</h2><small>Completed checkouts</small></div></div>");
            sb.append("<div class='col-md-3'><div class='card border-0 shadow-sm bg-info text-dark p-3 rounded-4'><h5>Catalog Items</h5><h2>").append(totalProducts).append("</h2><small>Physical & Digital</small></div></div>");
            sb.append("<div class='col-md-3'><div class='card border-0 shadow-sm bg-warning text-dark p-3 rounded-4'><h5>Users & Customers</h5><h2>").append(totalUsers).append("</h2><small>Registered accounts</small></div></div>");
            sb.append("</div>");

            // Recent Orders Table
            sb.append("<div class='card border-0 shadow-sm rounded-4'><div class='card-header bg-white py-3'><h5 class='mb-0'>Customer Order Ledger</h5></div>");
            sb.append("<div class='card-body p-0'><div class='table-responsive'><table class='table table-hover align-middle mb-0'>");
            sb.append("<thead class='table-light'><tr><th>Order ID</th><th>Customer</th><th>Total</th><th>Payment</th><th>Status</th><th>Date</th></tr></thead><tbody>");

            if (orders.isEmpty()) {
                sb.append("<tr><td colspan='6' class='text-center py-4 text-muted'>No orders placed yet.</td></tr>");
            } else {
                for (Order o : orders) {
                    sb.append("<tr><td><strong>#").append(o.getOrderId()).append("</strong></td>")
                            .append("<td>").append(o.getCustomerName()).append("<br><small class='text-muted'>").append(o.getCustomerEmail()).append("</small></td>")
                            .append("<td><strong>₹").append(String.format("%.2f", o.getNetPayable())).append("</strong></td>")
                            .append("<td><span class='badge bg-secondary'>").append(o.getPaymentMethod()).append("</span></td>")
                            .append("<td><span class='badge bg-primary'>").append(o.getOrderStatus()).append("</span></td>")
                            .append("<td>").append(o.getOrderDate().toLocalDate()).append("</td></tr>");
                }
            }
            sb.append("</tbody></table></div></div></div></div>");

            String html = renderPageTemplate("Admin Dashboard — ShopSphere", sb.toString());
            sendHtmlResponse(exchange, html, 200);
        }
    }

    private class ApiProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            List<Product> products = productService.getAllProducts();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"brand\":\"%s\",\"price\":%.2f,\"discount\":%.2f,\"finalPrice\":%.2f,\"stock\":%d,\"rating\":%.2f,\"type\":\"%s\"}",
                        p.getProductId(), p.getName().replace("\"", "\\\""), p.getBrand(),
                        p.getPrice(), p.getDiscountPercent(), p.getDiscountedPrice(), p.getStock(), p.getRating(), p.getProductType()));
                if (i < products.size() - 1) json.append(",");
            }
            json.append("]");

            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // =========================================================================
    // TEMPLATE RENDERING
    // =========================================================================

    private String renderStorefrontHtml(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='bg-primary bg-gradient text-white py-5 mb-5 rounded-4 shadow-sm'>"
                + "<div class='container text-center py-4'>"
                + "<h1 class='display-4 fw-bold mb-3'>Welcome to ShopSphere 🛍️</h1>"
                + "<p class='lead mb-4'>Advanced Java Enterprise E-Commerce Platform | Real-Time Inventory & Instant Checkout</p>"
                + "<div class='d-flex justify-content-center gap-3'>"
                + "<a href='/products' class='btn btn-light btn-lg px-4 fw-semibold text-primary shadow-sm'>Browse All Products</a>"
                + "<a href='/admin' class='btn btn-outline-light btn-lg px-4'>Admin Dashboard</a>"
                + "</div></div></div>");

        sb.append("<div class='container mb-5'>");
        sb.append("<div class='d-flex justify-content-between align-items-center mb-4'>");
        sb.append("<h3 class='fw-bold mb-0'>🔥 Featured Products</h3>");
        sb.append("<a href='/products' class='btn btn-outline-primary btn-sm'>View All &rarr;</a></div>");
        sb.append(renderProductCardsGrid(products));
        sb.append("</div>");
        return sb.toString();
    }

    private String renderProductCardsGrid(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='row row-cols-1 row-cols-md-3 g-4'>");
        for (Product p : products) {
            String badge = (p instanceof DigitalProduct)
                    ? "<span class='badge bg-info text-dark position-absolute top-0 end-0 m-3'>DIGITAL DOWNLOAD</span>"
                    : "<span class='badge bg-success position-absolute top-0 end-0 m-3'>" + (int) p.getDiscountPercent() + "% OFF</span>";

            sb.append("<div class='col'><div class='card h-100 shadow-sm border-0 rounded-4 position-relative overflow-hidden'>");
            sb.append(badge);
            sb.append("<div class='card-body d-flex flex-column p-4'>");
            sb.append("<span class='text-muted small text-uppercase fw-bold mb-1'>").append(p.getBrand() != null ? p.getBrand() : "Generic").append("</span>");
            sb.append("<h5 class='card-title fw-bold text-truncate'>").append(p.getName()).append("</h5>");
            sb.append("<p class='card-text text-muted small flex-grow-1'>").append(p.getDescription()).append("</p>");

            sb.append("<div class='mb-2 text-warning small'>★ ").append(p.getRating()).append(" / 5.0</div>");

            sb.append("<div class='d-flex justify-content-between align-items-baseline mt-2 mb-3'>");
            sb.append("<div><span class='h4 fw-bold text-dark'>₹").append(String.format("%.2f", p.getDiscountedPrice())).append("</span>");
            if (p.getDiscountPercent() > 0) {
                sb.append(" <span class='text-muted text-decoration-line-through small'>₹").append(String.format("%.2f", p.getPrice())).append("</span>");
            }
            sb.append("</div>");
            sb.append("<span class='badge ").append(p.getStock() > 5 ? "bg-light text-dark" : "bg-danger").append("'>Stock: ").append(p.getStock()).append("</span>");
            sb.append("</div>");

            sb.append("<form method='POST' action='/cart'>");
            sb.append("<input type='hidden' name='action' value='add'>");
            sb.append("<input type='hidden' name='productId' value='").append(p.getProductId()).append("'>");
            sb.append("<button type='submit' class='btn btn-primary w-100 rounded-pill fw-semibold'><i class='bi bi-cart-plus'></i> Add to Cart</button>");
            sb.append("</form>");

            sb.append("</div></div></div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String renderCartHtml(Customer customer) {
        List<OrderItem> cart = customer.getCart();
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='container py-4'>");
        sb.append("<h2 class='mb-4'><i class='bi bi-cart3 text-primary'></i> Your Shopping Cart</h2>");

        if (cart.isEmpty()) {
            sb.append("<div class='card p-5 text-center border-0 shadow-sm rounded-4'>");
            sb.append("<div class='display-1 text-muted mb-3'>🛒</div>");
            sb.append("<h4>Your cart is empty!</h4>");
            sb.append("<p class='text-muted'>Explore our catalog to find exciting deals.</p>");
            sb.append("<div><a href='/products' class='btn btn-primary btn-lg rounded-pill px-4'>Start Shopping</a></div>");
            sb.append("</div></div>");
            return sb.toString();
        }

        double subtotal = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();

        sb.append("<div class='row g-4'><div class='col-lg-8'>");
        sb.append("<div class='card border-0 shadow-sm rounded-4'><div class='card-body p-0'>");
        sb.append("<table class='table table-hover align-middle mb-0'><thead class='table-light'><tr><th>Item</th><th>Price</th><th>Qty</th><th>Subtotal</th><th>Action</th></tr></thead><tbody>");

        for (OrderItem item : cart) {
            sb.append("<tr>");
            sb.append("<td><strong>").append(item.getProduct().getName()).append("</strong></td>");
            sb.append("<td>₹").append(String.format("%.2f", item.getUnitPrice())).append("</td>");
            sb.append("<td><span class='badge bg-secondary fs-6'>").append(item.getQuantity()).append("</span></td>");
            sb.append("<td><strong>₹").append(String.format("%.2f", item.getSubtotal())).append("</strong></td>");
            sb.append("<td><form method='POST' action='/cart'><input type='hidden' name='action' value='remove'><input type='hidden' name='productId' value='").append(item.getProduct().getProductId()).append("'><button class='btn btn-sm btn-outline-danger'><i class='bi bi-trash'></i></button></form></td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table></div></div></div>");

        // Order Summary Sidebar
        sb.append("<div class='col-lg-4'>");
        sb.append("<div class='card border-0 shadow-sm rounded-4 p-4'>");
        sb.append("<h4 class='fw-bold mb-3'>Order Summary</h4>");
        sb.append("<div class='d-flex justify-content-between mb-2'><span>Items Subtotal:</span><span>₹").append(String.format("%.2f", subtotal)).append("</span></div>");
        
        List<Coupon> activeCoupons = couponService.getAllCoupons();
        if (!activeCoupons.isEmpty()) {
            sb.append("<div class='mb-2 small'><strong>Available Promos:</strong> ");
            for (Coupon c : activeCoupons) {
                sb.append("<span class='badge bg-success-subtle text-success border border-success-subtle me-1'>").append(c.getCode()).append("</span>");
            }
            sb.append("</div>");
        }
        sb.append("<hr>");

        sb.append("<form method='POST' action='/checkout'>");
        sb.append("<div class='mb-3'><label class='form-label small fw-bold'>Delivery Address</label><input type='text' name='address' class='form-control' value='Flat 402, MG Road, Bengaluru - 560001' required></div>");
        sb.append("<div class='mb-3'><label class='form-label small fw-bold'>Promo / Coupon Code</label><input type='text' name='coupon' class='form-control' placeholder='WELCOME10' value='WELCOME10'></div>");
        sb.append("<div class='mb-3'><label class='form-label small fw-bold'>Payment Method</label><select name='paymentMethod' class='form-select'><option value='UPI_NETBANKING'>UPI / Net Banking</option><option value='CREDIT_CARD'>Credit / Debit Card</option><option value='CASH_ON_DELIVERY'>Cash On Delivery</option></select></div>");
        sb.append("<button type='submit' class='btn btn-success btn-lg w-100 rounded-pill fw-bold shadow-sm'><i class='bi bi-shield-check'></i> Place Order Now</button>");
        sb.append("</form>");

        sb.append("</div></div></div></div>");
        return sb.toString();
    }

    private String renderPageTemplate(String title, String bodyContent) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>" + title + "</title>"
                + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
                + "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css'>"
                + "<style>"
                + "body { font-family: 'Segoe UI', system-ui, sans-serif; background-color: #f8fafc; color: #1e293b; }"
                + ".navbar-brand { font-weight: 800; letter-spacing: -0.5px; }"
                + ".card { transition: transform 0.2s ease, box-shadow 0.2s ease; }"
                + ".card:hover { transform: translateY(-4px); box-shadow: 0 12px 24px -10px rgba(0,0,0,0.15)!important; }"
                + "</style></head><body>"
                + "<nav class='navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm py-3'>"
                + "<div class='container'>"
                + "<a class='navbar-brand text-primary fs-3' href='/'><i class='bi bi-bag-heart-fill'></i> ShopSphere</a>"
                + "<button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav'><span class='navbar-toggler-icon'></span></button>"
                + "<div class='collapse navbar-collapse' id='navbarNav'>"
                + "<ul class='navbar-nav me-auto mb-2 mb-lg-0 ms-4'>"
                + "<li class='nav-item'><a class='nav-link fw-semibold' href='/'><i class='bi bi-house'></i> Home</a></li>"
                + "<li class='nav-item'><a class='nav-link fw-semibold' href='/products'><i class='bi bi-grid'></i> Products</a></li>"
                + "<li class='nav-item'><a class='nav-link fw-semibold' href='/cart'><i class='bi bi-cart3'></i> Cart</a></li>"
                + "<li class='nav-item'><a class='nav-link fw-semibold text-warning' href='/admin'><i class='bi bi-shield-lock'></i> Admin Portal</a></li>"
                + "</ul>"
                + "<span class='navbar-text text-white-50 small me-3'><i class='bi bi-person-circle'></i> rahul@example.com (50 pts)</span>"
                + "<a href='/cart' class='btn btn-primary rounded-pill px-3'><i class='bi bi-cart-fill'></i> Cart</a>"
                + "</div></div></nav>"
                + bodyContent
                + "<footer class='bg-dark text-white-50 py-4 mt-5 text-center small'>"
                + "<div class='container'><p class='mb-1'>&copy; 2026 ShopSphere Enterprise E-Commerce System | RTU Java Capstone</p>"
                + "<p class='mb-0'>Running on <strong>http://localhost:8080</strong></p></div></footer>"
                + "<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>"
                + "</body></html>";
    }

    private void sendHtmlResponse(HttpExchange exchange, String html, int status) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String formData = br.readLine();
            if (formData != null && !formData.isEmpty()) {
                for (String pair : formData.split("&")) {
                    String[] parts = pair.split("=");
                    if (parts.length == 2) {
                        map.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                                URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
                    }
                }
            }
        }
        return map;
    }
}
