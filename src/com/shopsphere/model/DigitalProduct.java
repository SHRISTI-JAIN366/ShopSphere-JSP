package com.shopsphere.model;

/**
 * Concrete Digital Product representing downloadable assets, eBooks, or courses.
 */
public class DigitalProduct extends Product {
    private static final long serialVersionUID = 1L;

    private String downloadUrl;
    private double fileSizeMb;

    public DigitalProduct() {
        super();
        this.stock = 99999; // Digital products have virtually unlimited inventory
    }

    public DigitalProduct(int productId, int categoryId, String name, String brand, String description,
                          double price, double discountPercent, int stock, String imageUrl, double rating,
                          String downloadUrl, double fileSizeMb) {
        super(productId, categoryId, name, brand, description, price, discountPercent, Math.max(stock, 1000), imageUrl, rating);
        this.downloadUrl = downloadUrl;
        this.fileSizeMb = Math.max(0.0, fileSizeMb);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        this.fileSizeMb = fileSizeMb;
    }

    @Override
    public String getProductType() {
        return "DIGITAL";
    }

    @Override
    public double calculateDeliveryCost() {
        return 0.0; // Digital products have instant free delivery
    }

    @Override
    public String getDeliveryInfo() {
        return String.format("Instant Digital Download | File Size: %.1f MB", fileSizeMb);
    }
}
