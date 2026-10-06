package com.shopsphere.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer class representing shoppers with carts, orders, and addresses.
 */
public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private ArrayList<Address> addresses;
    private ArrayList<OrderItem> cart;
    private int loyaltyPoints;

    public Customer() {
        super();
        this.role = "CUSTOMER";
        this.addresses = new ArrayList<>();
        this.cart = new ArrayList<>();
        this.loyaltyPoints = 50; // Starting bonus points
    }

    public Customer(int userId, String name, String email, String passwordHash, String phone) {
        super(userId, name, email, passwordHash, phone, "CUSTOMER");
        this.addresses = new ArrayList<>();
        this.cart = new ArrayList<>();
        this.loyaltyPoints = 50;
    }

    @Override
    public void displayDashboardWelcome() {
        System.out.println("==================================================");
        System.out.println(" Welcome to ShopSphere, " + name + "! 🛍️");
        System.out.println(" Loyalty Points Available: " + loyaltyPoints);
        System.out.println(" Cart Items: " + getCartItemCount());
        System.out.println("==================================================");
    }

    public List<Address> getAddresses() {
        if (addresses == null) addresses = new ArrayList<>();
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = (addresses != null) ? new ArrayList<>(addresses) : new ArrayList<>();
    }

    public void addAddress(Address address) {
        if (this.addresses == null) this.addresses = new ArrayList<>();
        this.addresses.add(address);
    }

    public List<OrderItem> getCart() {
        if (cart == null) cart = new ArrayList<>();
        return cart;
    }

    public void setCart(List<OrderItem> cart) {
        this.cart = (cart != null) ? new ArrayList<>(cart) : new ArrayList<>();
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    public int getCartItemCount() {
        if (cart == null) return 0;
        return cart.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public void addToCart(Product product, int quantity) {
        if (cart == null) cart = new ArrayList<>();
        for (OrderItem item : cart) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cart.add(new OrderItem(product, quantity));
    }

    public void removeFromCart(int productId) {
        if (cart != null) {
            cart.removeIf(item -> item.getProduct().getProductId() == productId);
        }
    }

    public void clearCart() {
        if (cart != null) {
            cart.clear();
        }
    }
}
