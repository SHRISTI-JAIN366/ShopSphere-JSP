package com.shopsphere.dao;

import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Order DAO for transactionally persisting orders and line items.
 */
public class OrderDAO {

    public boolean placeOrder(Order order) {
        String orderSql = "INSERT INTO orders (user_id, total_amount, discount_amount, shipping_fee, net_payable, " +
                "payment_method, payment_status, order_status, shipping_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, order.getCustomerId());
                psOrder.setDouble(2, order.getGrossTotal());
                psOrder.setDouble(3, order.getDiscountAmount());
                psOrder.setDouble(4, order.getShippingFee());
                psOrder.setDouble(5, order.getNetPayable());
                psOrder.setString(6, order.getPaymentMethod());
                psOrder.setString(7, order.getPaymentStatus());
                psOrder.setString(8, order.getOrderStatus());
                psOrder.setString(9, order.getShippingAddress());

                psOrder.executeUpdate();
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        order.setOrderId(rs.getInt(1));
                    }
                }
            }

            try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                for (OrderItem item : order.getItems()) {
                    psItem.setInt(1, order.getOrderId());
                    psItem.setInt(2, item.getProduct().getProductId());
                    psItem.setString(3, item.getProduct().getName());
                    psItem.setInt(4, item.getQuantity());
                    psItem.setDouble(5, item.getUnitPrice());
                    psItem.setDouble(6, item.getSubtotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit(); // Commit Transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Database error during order transaction: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.name as customer_name, u.email as customer_email " +
                "FROM orders o JOIN users u ON o.user_id = u.user_id ORDER BY o.order_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("order_id"));
                o.setCustomerId(rs.getInt("user_id"));
                o.setCustomerName(rs.getString("customer_name"));
                o.setCustomerEmail(rs.getString("customer_email"));
                o.setGrossTotal(rs.getDouble("total_amount"));
                o.setDiscountAmount(rs.getDouble("discount_amount"));
                o.setShippingFee(rs.getDouble("shipping_fee"));
                o.setNetPayable(rs.getDouble("net_payable"));
                o.setPaymentMethod(rs.getString("payment_method"));
                o.setPaymentStatus(rs.getString("payment_status"));
                o.setOrderStatus(rs.getString("order_status"));
                o.setShippingAddress(rs.getString("shipping_address"));
                Timestamp ts = rs.getTimestamp("order_date");
                if (ts != null) {
                    o.setOrderDate(ts.toLocalDateTime());
                }
                orders.add(o);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching orders: " + e.getMessage());
        }
        return orders;
    }
}
