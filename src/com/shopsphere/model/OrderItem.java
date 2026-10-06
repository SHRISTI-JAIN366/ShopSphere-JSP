package com.shopsphere.model;

import java.io.Serializable;

/**
 * Represents an individual line item in a Cart or Order.
 */
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderItemId;
    private int orderId;
    private Product product;
    private int quantity;
    private double unitPrice; // Snapshot of discounted price at time of purchase

    public OrderItem() {}

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = Math.max(1, quantity);
        this.unitPrice = product != null ? product.getDiscountedPrice() : 0.0;
    }

    public OrderItem(int orderItemId, int orderId, Product product, int quantity, double unitPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.product = product;
        this.quantity = Math.max(1, quantity);
        this.unitPrice = unitPrice;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null && this.unitPrice == 0.0) {
            this.unitPrice = product.getDiscountedPrice();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public double getOriginalSubtotal() {
        return (product != null ? product.getPrice() : unitPrice) * quantity;
    }

    public double getSavings() {
        return getOriginalSubtotal() - getSubtotal();
    }

    @Override
    public String toString() {
        return String.format("%-35s x %2d @ ₹%.2f = ₹%.2f",
                product != null ? product.getName() : "Item",
                quantity, unitPrice, getSubtotal());
    }
}
