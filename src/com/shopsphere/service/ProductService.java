package com.shopsphere.service;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Product Service managing product catalog using Java Collections:
 * Map<Integer, Product> for O(1) lookups, List<Product> for sorting, and Streams for filtering.
 */
public class ProductService {

    private final Map<Integer, Product> productCatalog;
    private final Map<Integer, Category> categoryMap;

    public ProductService() {
        this.productCatalog = new ConcurrentHashMap<>();
        this.categoryMap = new ConcurrentHashMap<>();
    }

    public void addCategory(Category category) {
        if (category != null) {
            categoryMap.put(category.getCategoryId(), category);
        }
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categoryMap.values());
    }

    public void addProduct(Product product) {
        if (product != null) {
            productCatalog.put(product.getProductId(), product);
        }
    }

    public Product getProductById(int productId) throws ProductNotFoundException {
        Product p = productCatalog.get(productId);
        if (p == null) {
            throw new ProductNotFoundException(productId);
        }
        return p;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productCatalog.values());
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productCatalog.values().stream()
                .filter(p -> p.matchesKeyword(keyword))
                .collect(Collectors.toList());
    }

    public List<Product> filterByCategory(int categoryId) {
        return productCatalog.values().stream()
                .filter(p -> p.getCategoryId() == categoryId)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsSortedByPrice(boolean ascending) {
        return productCatalog.values().stream()
                .sorted(ascending ? Comparator.comparingDouble(Product::getDiscountedPrice)
                                  : Comparator.comparingDouble(Product::getDiscountedPrice).reversed())
                .collect(Collectors.toList());
    }

    public List<Product> getProductsSortedByRating() {
        return productCatalog.values().stream()
                .sorted(Comparator.comparingDouble(Product::getRating).reversed())
                .collect(Collectors.toList());
    }

    public void restockProduct(int productId, int quantity) throws ProductNotFoundException {
        Product p = getProductById(productId);
        p.addStock(quantity);
    }

    public void deductProductStock(int productId, int quantity) throws ProductNotFoundException, InsufficientStockException {
        Product p = getProductById(productId);
        p.deductStock(quantity);
    }

    public boolean removeProduct(int productId) {
        return productCatalog.remove(productId) != null;
    }

    public int getNextProductId() {
        return productCatalog.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }
}
