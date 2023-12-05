package com.example.livraison.model;

import java.util.ArrayList;

public class Order {
    private String userId;
    private ArrayList<Product> orderedProducts;
    private String address;
    private String deliveryDate;

    public Order() {
    }

    public Order(String userId, ArrayList<Product> orderedProducts, String address, String deliveryDate) {
        this.userId = userId;
        this.orderedProducts = orderedProducts;
        this.address = address;
        this.deliveryDate = deliveryDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Product> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(ArrayList<Product> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
