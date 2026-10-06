package com.shopsphere.interfaces;

/**
 * Interface for entities that support financial checkout and payment calculations.
 */
public interface Payable {
    double calculateGrossTotal();
    double calculateTotalDiscount();
    double calculateShippingFee();
    double calculateNetPayable();
    boolean processPayment(String paymentMethod);
}
