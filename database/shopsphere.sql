-- =========================================================
-- SHOPSPHERE E-COMMERCE DATABASE SCHEMA
-- Compatible with MySQL 8.0+ / MariaDB
-- =========================================================

CREATE DATABASE IF NOT EXISTS shopsphere;
USE shopsphere;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'ADMIN') DEFAULT 'CUSTOMER',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Addresses Table
CREATE TABLE IF NOT EXISTS addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'India',
    address_type ENUM('HOME', 'WORK', 'BILLING', 'SHIPPING') DEFAULT 'SHIPPING',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 4. Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    product_type ENUM('PHYSICAL', 'DIGITAL') DEFAULT 'PHYSICAL',
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    weight_kg DECIMAL(6,2) DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    download_url VARCHAR(500),
    file_size_mb DECIMAL(6,2) DEFAULT 0.00,
    rating DECIMAL(3,2) DEFAULT 4.50,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
);

-- 5. Coupons Table
CREATE TABLE IF NOT EXISTS coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_percentage DECIMAL(5,2) NOT NULL,
    max_discount DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) NOT NULL,
    expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE
);

-- 6. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    net_payable DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) DEFAULT 'COMPLETED',
    order_status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PROCESSING',
    shipping_address TEXT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 8. Reviews Table
CREATE TABLE IF NOT EXISTS reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- =========================================================
-- SEED DATA
-- =========================================================

-- Insert Admin & Customer Users (passwords hashed using SHA-256 for 'admin123' and 'customer123')
-- admin123: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- user123:  a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
INSERT INTO users (name, email, password_hash, phone, role) VALUES
('System Administrator', 'admin@shopsphere.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '9876543210', 'ADMIN'),
('Rahul Sharma', 'rahul@example.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '9123456780', 'CUSTOMER'),
('Priya Patel', 'priya@example.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', '9871234560', 'CUSTOMER');

-- Insert Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Smartphones, Laptops, Headphones and Gadgets'),
('Fashion & Apparel', 'Trendy clothing, footwear and accessories'),
('Home & Living', 'Furniture, decor, lighting and kitchen essentials'),
('Books & Digital Courses', 'Printed books, eBooks, and certification video guides');

-- Insert Products (Physical and Digital)
INSERT INTO products (category_id, product_type, name, brand, description, price, discount_percent, stock, image_url, weight_kg, shipping_fee, rating) VALUES
(1, 'PHYSICAL', 'MacBook Air M3 (16GB, 512GB SSD)', 'Apple', 'Apple M3 chip, 13.6-inch Liquid Retina Display, Space Grey', 114900.00, 10.00, 25, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8', 1.24, 0.00, 4.9),
(1, 'PHYSICAL', 'Sony WH-1000XM5 Wireless ANC Headphones', 'Sony', 'Industry-leading noise canceling with Auto NC Optimizer, 30hr battery', 29990.00, 15.00, 40, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e', 0.25, 100.00, 4.8),
(1, 'PHYSICAL', 'Logitech MX Master 3S Wireless Mouse', 'Logitech', 'Quiet clicks, 8K DPI tracking on glass, Ergonomic sculpted design', 8995.00, 5.00, 50, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46', 0.14, 50.00, 4.7),
(2, 'PHYSICAL', 'Classic Premium Denim Jacket', 'Levi''s', '100% Cotton, regular fit with Sherpa lining, timeless indigo wash', 4499.00, 20.00, 60, 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0', 0.80, 80.00, 4.5),
(2, 'PHYSICAL', 'Nike Air Zoom Pegasus 40 Running Shoes', 'Nike', 'Responsive cushioning, engineered mesh upper, lightweight daily trainer', 10495.00, 12.00, 30, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', 0.65, 0.00, 4.8),
(3, 'PHYSICAL', 'Minimalist Scandinavian Oak Coffee Table', 'IKEA', 'Solid oak veneer with sturdy metal frame, water-resistant finish', 12999.00, 10.00, 15, 'https://images.unsplash.com/photo-1533090161767-e6ffed986c88', 12.50, 400.00, 4.6),
(4, 'PHYSICAL', 'Effective Java (3rd Edition)', 'Joshua Bloch', 'Comprehensive guide to best practices for Java platform programming', 3200.00, 25.00, 45, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c', 0.70, 60.00, 4.9);

-- Insert Digital Products
INSERT INTO products (category_id, product_type, name, brand, description, price, discount_percent, stock, image_url, download_url, file_size_mb, rating) VALUES
(4, 'DIGITAL', 'Full-Stack Java Enterprise Mastery Course (eBook + Video)', 'ShopSphere Academy', '120 hours of in-depth Spring, Jakarta EE, Microservices & Cloud deployment', 1999.00, 40.00, 9999, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3', 'https://downloads.shopsphere.com/courses/java-enterprise-mastery.zip', 450.00, 4.95),
(4, 'DIGITAL', 'Clean Code & Design Patterns Architecture Guide', 'TechPress', 'Practical handbook with 50+ real-world refactoring recipes in Java', 799.00, 20.00, 9999, 'https://images.unsplash.com/photo-1498050108023-c5249f4df085', 'https://downloads.shopsphere.com/books/clean-code-patterns.pdf', 35.00, 4.85);

-- Insert Discount Coupons
INSERT INTO coupons (code, discount_percentage, max_discount, min_order_amount, expiry_date, is_active) VALUES
('WELCOME10', 10.00, 1000.00, 2000.00, '2027-12-31', TRUE),
('FESTIVE25', 25.00, 5000.00, 10000.00, '2027-12-31', TRUE),
('FLAT500', 15.00, 500.00, 1500.00, '2027-12-31', TRUE);
