package com.shopsphere.model;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.interfaces.Discountable;
import com.shopsphere.interfaces.InventoryOperations;
import com.shopsphere.interfaces.Searchable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract Base Product class demonstrating OOP Principles:
 * Abstraction, Encapsulation, Polymorphism, and Interfaces.
 */
public abstract class Product implements Discountable, Searchable, InventoryOperations, Serializable, Comparable<Product> {
    private static final long serialVersionUID = 1L;

    protected int productId;
    protected int categoryId;
    protected String name;
    protected String brand;
    protected String description;
    protected double price;
    protected double discountPercent;
    protected int stock;
    protected String imageUrl;
    protected double rating;

    public Product() {
        this.rating = 4.5;
        this.discountPercent = 0.0;
    }

    public Product(int productId, int categoryId, String name, String brand, String description,
                   double price, double discountPercent, int stock, String imageUrl, double rating) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = Math.max(0.0, price);
        this.discountPercent = Math.max(0.0, Math.min(100.0, discountPercent));
        this.stock = Math.max(0, stock);
        this.imageUrl = imageUrl != null ? imageUrl : "default_product.png";
        this.rating = rating;
    }

    // Abstract method to be implemented by PhysicalProduct and DigitalProduct
    public abstract String getProductType();
    public abstract double calculateDeliveryCost();
    public abstract String getDeliveryInfo();

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = Math.max(0.0, price);
    }

    @Override
    public double getDiscountPercent() {
        return discountPercent;
    }

    @Override
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = Math.max(0.0, Math.min(100.0, discountPercent));
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = Math.max(0, stock);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Implementation of Discountable Interface
    @Override
    public double getDiscountedPrice() {
        return price * (1.0 - (discountPercent / 100.0));
    }

    @Override
    public double calculateSavings() {
        return price - getDiscountedPrice();
    }

    // Implementation of Searchable Interface
    @Override
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return true;
        String query = keyword.toLowerCase().trim();
        return (name != null && name.toLowerCase().contains(query)) ||
               (brand != null && brand.toLowerCase().contains(query)) ||
               (description != null && description.toLowerCase().contains(query));
    }

    // Implementation of InventoryOperations Interface
    @Override
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }

    @Override
    public synchronized void deductStock(int quantity) throws InsufficientStockException {
        if (quantity <= 0) return;
        if (!hasStock(quantity)) {
            throw new InsufficientStockException(this.name, this.stock, quantity);
        }
        this.stock -= quantity;
    }

    @Override
    public synchronized void addStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
        }
    }

    @Override
    public int compareTo(Product other) {
        return Double.compare(this.getDiscountedPrice(), other.getDiscountedPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - ₹%.2f (%.0f%% OFF -> ₹%.2f) | Stock: %d | ★ %.1f",
                productId, name, brand != null ? brand : "Generic",
                price, discountPercent, getDiscountedPrice(), stock, rating);
    }
}
