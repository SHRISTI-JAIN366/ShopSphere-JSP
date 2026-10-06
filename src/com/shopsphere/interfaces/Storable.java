package com.shopsphere.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for repository and persistence storage operations.
 * @param <T> Model type
 * @param <ID> Key type (e.g. Integer, String)
 */
public interface Storable<T, ID> {
    void save(T item);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean update(T item);
    boolean deleteById(ID id);
}
