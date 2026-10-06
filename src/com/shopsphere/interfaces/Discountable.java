package com.shopsphere.interfaces;

/**
 * Interface representing items or entities that can receive discount calculations.
 */
public interface Discountable {
    double getDiscountPercent();
    void setDiscountPercent(double discountPercent);
    double getDiscountedPrice();
    double calculateSavings();
}
