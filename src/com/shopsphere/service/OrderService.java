package com.shopsphere.service;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.exception.InvalidCouponException;
import com.shopsphere.exception.OrderNotFoundException;
import com.shopsphere.model.Customer;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Order Service orchestrating order placement, stock updates, invoices, and analytics.
 */
public class OrderService {

    private final Map<Integer, Order> orderMap;
    private final ProductService productService;
    private final CouponService couponService;

    public OrderService(ProductService productService, CouponService couponService) {
        this.orderMap = new ConcurrentHashMap<>();
        this.productService = productService;
        this.couponService = couponService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public CouponService getCouponService() {
        return couponService;
    }

    public synchronized Order placeOrder(Customer customer, List<OrderItem> items, String shippingAddress,
                                        String couponCode, String paymentMethod)
            throws InsufficientStockException, InvalidCouponException {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with an empty cart.");
        }

        // 1. Verify stock availability for all items first
        for (OrderItem item : items) {
            if (!item.getProduct().hasStock(item.getQuantity())) {
                throw new InsufficientStockException(item.getProduct().getName(),
                        item.getProduct().getStock(), item.getQuantity());
            }
        }

        // 2. Deduct inventory stock
        for (OrderItem item : items) {
            item.getProduct().deductStock(item.getQuantity());
        }

        // 3. Create Order
        int nextOrderId = orderMap.size() + 1001;
        Order order = new Order(nextOrderId, customer.getUserId(), customer.getName(), customer.getEmail(),
                items, shippingAddress, couponCode);

        // 4. Process Coupon Discount if provided
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            double discount = couponService.validateAndCalculateDiscount(couponCode, order.getGrossTotal());
            order.setDiscountAmount(discount);
        }

        // 5. Process Payment
        order.processPayment(paymentMethod);

        // 6. Award Loyalty Points to Customer (1 point per ₹100 spent)
        int pointsEarned = (int) (order.getNetPayable() / 100.0);
        customer.addLoyaltyPoints(pointsEarned);

        // 7. Store Order
        orderMap.put(order.getOrderId(), order);
        return order;
    }

    public void addOrder(Order order) {
        if (order != null) {
            orderMap.put(order.getOrderId(), order);
        }
    }

    public Order getOrderById(int orderId) throws OrderNotFoundException {
        Order o = orderMap.get(orderId);
        if (o == null) {
            throw new OrderNotFoundException(orderId);
        }
        return o;
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        return orderMap.values().stream()
                .filter(o -> o.getCustomerId() == customerId)
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() {
        return orderMap.values().stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .collect(Collectors.toList());
    }

    public void updateOrderStatus(int orderId, String newStatus) throws OrderNotFoundException {
        Order o = getOrderById(orderId);
        o.setOrderStatus(newStatus);
    }

    public double calculateTotalRevenue() {
        return orderMap.values().stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getOrderStatus()))
                .mapToDouble(Order::getNetPayable)
                .sum();
    }
}
