package com.shopsphere.exception;

public class ProductNotFoundException extends ShopSphereException {
    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(int productId) {
        super("Product with ID " + productId + " was not found in the catalog.");
    }
}
