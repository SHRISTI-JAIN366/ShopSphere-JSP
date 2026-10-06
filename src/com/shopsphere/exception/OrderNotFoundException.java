package com.shopsphere.exception;

public class OrderNotFoundException extends ShopSphereException {
    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(int orderId) {
        super("Order #" + orderId + " was not found in the system.");
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
