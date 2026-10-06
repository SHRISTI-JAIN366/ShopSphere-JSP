package com.shopsphere.model;

/**
 * Concrete Physical Product representing tangible goods with shipping and weight metrics.
 */
public class PhysicalProduct extends Product {
    private static final long serialVersionUID = 1L;

    private double weightKg;
    private double shippingFee;

    public PhysicalProduct() {
        super();
    }

    public PhysicalProduct(int productId, int categoryId, String name, String brand, String description,
                           double price, double discountPercent, int stock, String imageUrl, double rating,
                           double weightKg, double shippingFee) {
        super(productId, categoryId, name, brand, description, price, discountPercent, stock, imageUrl, rating);
        this.weightKg = Math.max(0.0, weightKg);
        this.shippingFee = Math.max(0.0, shippingFee);
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = Math.max(0.0, weightKg);
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = Math.max(0.0, shippingFee);
    }

    @Override
    public String getProductType() {
        return "PHYSICAL";
    }

    @Override
    public double calculateDeliveryCost() {
        // Free shipping if order price exceeds ₹10,000, otherwise return flat/weight fee
        if (getDiscountedPrice() >= 10000.0) {
            return 0.0;
        }
        return shippingFee;
    }

    @Override
    public String getDeliveryInfo() {
        return String.format("Standard Delivery (2-4 business days) | Weight: %.2f kg | Shipping: %s",
                weightKg, calculateDeliveryCost() == 0 ? "FREE" : "₹" + calculateDeliveryCost());
    }
}
