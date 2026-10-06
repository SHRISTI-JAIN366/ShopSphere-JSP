package com.shopsphere.exception;

public class AuthenticationException extends ShopSphereException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
