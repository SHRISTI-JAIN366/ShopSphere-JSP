package com.shopsphere.service;

import com.shopsphere.exception.InvalidCouponException;
import com.shopsphere.model.Coupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coupon Service for managing promo discount codes.
 */
public class CouponService {

    private final Map<String, Coupon> couponMap;

    public CouponService() {
        this.couponMap = new ConcurrentHashMap<>();
    }

    public void addCoupon(Coupon coupon) {
        if (coupon != null && coupon.getCode() != null) {
            couponMap.put(coupon.getCode().toUpperCase().trim(), coupon);
        }
    }

    public Coupon getCoupon(String code) throws InvalidCouponException {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidCouponException("Coupon code cannot be empty.");
        }
        Coupon c = couponMap.get(code.toUpperCase().trim());
        if (c == null) {
            throw new InvalidCouponException("Coupon code '" + code + "' is invalid or does not exist.");
        }
        return c;
    }

    public double validateAndCalculateDiscount(String code, double orderTotal) throws InvalidCouponException {
        Coupon c = getCoupon(code);
        return c.calculateDiscount(orderTotal);
    }

    public List<Coupon> getAllCoupons() {
        return new ArrayList<>(couponMap.values());
    }
}
