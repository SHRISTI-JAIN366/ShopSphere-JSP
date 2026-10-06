package com.shopsphere.exception;

public class InsufficientStockException extends ShopSphereException {
    private static final long serialVersionUID = 1L;
    private final int availableStock;
    private final int requestedQuantity;

    public InsufficientStockException(String productName, int availableStock, int requestedQuantity) {
        super(String.format("Insufficient stock for '%s'. Available: %d, Requested: %d",
                productName, availableStock, requestedQuantity));
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
