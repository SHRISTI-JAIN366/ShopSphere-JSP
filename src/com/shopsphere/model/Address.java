package com.shopsphere.model;

import java.io.Serializable;

/**
 * Model representing customer delivery and billing addresses.
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private int addressId;
    private int userId;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;

    public Address() {
        this.country = "India";
    }

    public Address(int addressId, int userId, String street, String city, String state, String pincode, String country) {
        this.addressId = addressId;
        this.userId = userId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.country = (country != null && !country.trim().isEmpty()) ? country : "India";
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullFormattedAddress() {
        return String.format("%s, %s, %s - %s, %s", street, city, state, pincode, country);
    }

    @Override
    public String toString() {
        return getFullFormattedAddress();
    }
}
