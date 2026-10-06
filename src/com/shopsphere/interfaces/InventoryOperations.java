package com.shopsphere.interfaces;

import com.shopsphere.exception.InsufficientStockException;

/**
 * Interface defining inventory and stock management contracts.
 */
public interface InventoryOperations {
    boolean hasStock(int quantity);
    void deductStock(int quantity) throws InsufficientStockException;
    void addStock(int quantity);
}
