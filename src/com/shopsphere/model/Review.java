package com.shopsphere.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing customer feedback, ratings and reviews.
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private int reviewId;
    private int userId;
    private String userName;
    private int productId;
    private int rating; // 1 to 5 stars
    private String comment;
    private LocalDateTime createdAt;

    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(int reviewId, int userId, String userName, int productId, int rating, String comment) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.rating = Math.max(1, Math.min(5, rating));
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStarDisplay() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    @Override
    public String toString() {
        return String.format("%s by %s: \"%s\"", getStarDisplay(), userName, comment);
    }
}
