package com.shopsphere.dao;

import com.shopsphere.model.DigitalProduct;
import com.shopsphere.model.PhysicalProduct;
import com.shopsphere.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Product Data Access Object for JDBC persistence.
 * Uses PreparedStatements for all parameterized queries.
 */
public class ProductDAO {

    public boolean save(Product product) {
        String sql = "INSERT INTO products (category_id, product_type, name, brand, description, price, " +
                "discount_percent, stock, image_url, weight_kg, shipping_fee, download_url, file_size_mb, rating) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getProductType());
            ps.setString(3, product.getName());
            ps.setString(4, product.getBrand());
            ps.setString(5, product.getDescription());
            ps.setDouble(6, product.getPrice());
            ps.setDouble(7, product.getDiscountPercent());
            ps.setInt(8, product.getStock());
            ps.setString(9, product.getImageUrl());

            if (product instanceof PhysicalProduct) {
                PhysicalProduct p = (PhysicalProduct) product;
                ps.setDouble(10, p.getWeightKg());
                ps.setDouble(11, p.getShippingFee());
                ps.setNull(12, Types.VARCHAR);
                ps.setNull(13, Types.DECIMAL);
            } else if (product instanceof DigitalProduct) {
                DigitalProduct d = (DigitalProduct) product;
                ps.setNull(10, Types.DECIMAL);
                ps.setNull(11, Types.DECIMAL);
                ps.setString(12, d.getDownloadUrl());
                ps.setDouble(13, d.getFileSizeMb());
            }

            ps.setDouble(14, product.getRating());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setProductId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database error saving product: " + e.getMessage());
        }
        return false;
    }

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching products: " + e.getMessage());
        }
        return list;
    }

    public Optional<Product> getById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching product by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean updateStock(int productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating stock: " + e.getMessage());
            return false;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("product_id");
        int catId = rs.getInt("category_id");
        String type = rs.getString("product_type");
        String name = rs.getString("name");
        String brand = rs.getString("brand");
        String desc = rs.getString("description");
        double price = rs.getDouble("price");
        double discount = rs.getDouble("discount_percent");
        int stock = rs.getInt("stock");
        String img = rs.getString("image_url");
        double rating = rs.getDouble("rating");

        if ("DIGITAL".equalsIgnoreCase(type)) {
            String downloadUrl = rs.getString("download_url");
            double fileSize = rs.getDouble("file_size_mb");
            return new DigitalProduct(id, catId, name, brand, desc, price, discount, stock, img, rating, downloadUrl, fileSize);
        } else {
            double weight = rs.getDouble("weight_kg");
            double shipping = rs.getDouble("shipping_fee");
            return new PhysicalProduct(id, catId, name, brand, desc, price, discount, stock, img, rating, weight, shipping);
        }
    }
}
