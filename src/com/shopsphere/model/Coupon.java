package com.shopsphere.model;

import com.shopsphere.exception.InvalidCouponException;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model representing discount promo codes with qualification rules.
 */
public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int couponId;
    private String code;
    private double discountPercentage;
    private double maxDiscount;
    private double minOrderAmount;
    private LocalDate expiryDate;
    private boolean active;

    public Coupon() {
        this.active = true;
    }

    public Coupon(int couponId, String code, double discountPercentage, double maxDiscount,
                  double minOrderAmount, LocalDate expiryDate, boolean active) {
        this.couponId = couponId;
        this.code = code != null ? code.toUpperCase().trim() : "";
        this.discountPercentage = discountPercentage;
        this.maxDiscount = maxDiscount;
        this.minOrderAmount = minOrderAmount;
        this.expiryDate = expiryDate;
        this.active = active;
    }

    public double calculateDiscount(double cartTotal) throws InvalidCouponException {
        if (!active) {
            throw new InvalidCouponException("Coupon '" + code + "' is inactive or expired.");
        }
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            throw new InvalidCouponException("Coupon '" + code + "' has expired on " + expiryDate);
        }
        if (cartTotal < minOrderAmount) {
            throw new InvalidCouponException(String.format(
                    "Order total ₹%.2f does not meet minimum order of ₹%.2f for coupon '%s'.",
                    cartTotal, minOrderAmount, code));
        }

        double calculated = cartTotal * (discountPercentage / 100.0);
        return Math.min(calculated, maxDiscount);
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.toUpperCase().trim() : "";
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("[%s] %.0f%% OFF up to ₹%.2f (Min: ₹%.2f)",
                code, discountPercentage, maxDiscount, minOrderAmount);
    }
}
