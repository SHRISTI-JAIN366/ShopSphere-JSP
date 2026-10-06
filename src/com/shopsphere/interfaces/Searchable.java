package com.shopsphere.interfaces;

/**
 * Interface for catalog and search querying.
 */
public interface Searchable {
    boolean matchesKeyword(String keyword);
}
