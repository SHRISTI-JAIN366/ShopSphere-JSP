package com.shopsphere.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a product category.
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int categoryId;
    private String name;
    private String description;

    public Category() {}

    public Category(int categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", categoryId, name, description);
    }
}
