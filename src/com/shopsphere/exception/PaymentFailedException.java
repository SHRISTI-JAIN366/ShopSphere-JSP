package com.shopsphere.exception;

public class PaymentFailedException extends ShopSphereException {
    private static final long serialVersionUID = 1L;

    public PaymentFailedException(String message) {
        super(message);
    }
}
