package com.shopsphere.model;

import com.shopsphere.interfaces.Payable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity implementing Payable and Serializable.
 * Handles financial computations, item breakdowns, invoice generation, and status tracking.
 */
public class Order implements Payable, Serializable {
    private static final long serialVersionUID = 1L;

    private int orderId;
    private int customerId;
    private String customerName;
    private String customerEmail;
    private ArrayList<OrderItem> items;
    private double grossTotal;
    private double discountAmount;
    private String couponCode;
    private double shippingFee;
    private double netPayable;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String shippingAddress;
    private LocalDateTime orderDate;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.orderStatus = "PROCESSING";
        this.paymentStatus = "PAID";
    }

    public Order(int orderId, int customerId, String customerName, String customerEmail,
                 List<OrderItem> items, String shippingAddress, String couponCode) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        this.shippingAddress = shippingAddress;
        this.couponCode = couponCode;
        this.orderDate = LocalDateTime.now();
        this.orderStatus = "PROCESSING";
        this.paymentStatus = "PENDING";
        
        double gross = 0.0;
        double ship = 0.0;
        for (OrderItem it : this.items) {
            gross += it.getSubtotal();
            if (it.getProduct() != null) {
                ship += it.getProduct().calculateDeliveryCost();
            }
        }
        this.grossTotal = gross;
        this.shippingFee = ship;
        this.netPayable = Math.max(0.0, gross + ship);
    }

    public final void recalculateTotals() {
        this.grossTotal = calculateGrossTotal();
        this.shippingFee = calculateShippingFee();
        this.netPayable = calculateNetPayable();
    }

    @Override
    public double calculateGrossTotal() {
        if (items == null) return 0.0;
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    @Override
    public double calculateTotalDiscount() {
        return discountAmount;
    }

    @Override
    public double calculateShippingFee() {
        if (items == null) return 0.0;
        return items.stream()
                .map(OrderItem::getProduct)
                .mapToDouble(p -> p != null ? p.calculateDeliveryCost() : 0.0)
                .sum();
    }

    @Override
    public double calculateNetPayable() {
        double total = calculateGrossTotal() - discountAmount + calculateShippingFee();
        return Math.max(0.0, total);
    }

    @Override
    public boolean processPayment(String method) {
        this.paymentMethod = method;
        this.paymentStatus = "COMPLETED";
        this.netPayable = calculateNetPayable();
        return true;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<OrderItem> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        recalculateTotals();
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        recalculateTotals();
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getNetPayable() {
        return netPayable;
    }

    public void setNetPayable(double netPayable) {
        this.netPayable = netPayable;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String generateInvoiceSummary() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================================================\n");
        sb.append("                     SHOPSPHERE OFFICIAL INVOICE                        \n");
        sb.append("========================================================================\n");
        sb.append(String.format(" Order ID:        #%-20d Date: %s\n", orderId, orderDate != null ? orderDate.format(dtf) : "N/A"));
        sb.append(String.format(" Customer:        %s (%s)\n", customerName, customerEmail));
        sb.append(String.format(" Shipping to:     %s\n", shippingAddress));
        sb.append(String.format(" Order Status:    [%s] | Payment: [%s via %s]\n", orderStatus, paymentStatus, paymentMethod != null ? paymentMethod : "N/A"));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format(" %-4s %-38s %-6s %-10s %-10s\n", "#", "Item Name", "Qty", "Price", "Total"));
        sb.append("------------------------------------------------------------------------\n");

        int index = 1;
        for (OrderItem item : items) {
            String name = item.getProduct() != null ? item.getProduct().getName() : "Product";
            if (name.length() > 36) name = name.substring(0, 33) + "...";
            sb.append(String.format(" %-4d %-38s %-6d ₹%-9.2f ₹%-9.2f\n",
                    index++, name, item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));
        }

        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format(" Gross Items Total:                                   ₹%10.2f\n", grossTotal));
        if (discountAmount > 0) {
            sb.append(String.format(" Coupon Discount (%s):                               -₹%10.2f\n",
                    couponCode != null ? couponCode : "PROMO", discountAmount));
        }
        sb.append(String.format(" Shipping & Handling:                                 ₹%10.2f\n", shippingFee));
        sb.append("========================================================================\n");
        sb.append(String.format(" NET PAYABLE TOTAL:                                   ₹%10.2f\n", netPayable));
        sb.append("========================================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Order #%d | Customer: %s | Total: ₹%.2f | Status: %s",
                orderId, customerName, netPayable, orderStatus);
    }
}
