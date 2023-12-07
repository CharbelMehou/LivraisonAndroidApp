package com.example.livraison.model;

import java.util.ArrayList;

public class Order {
    private transient String tempId;
    private String userEmail;
    private ArrayList<Product> orderedProducts;
    private String address;
    private String deliveryDate;
    private boolean isValidateByPlaneur;
    private String state;
    private String driverSelected;

    public Order() {
    }

    public Order(String  tempId,String userEmail, ArrayList<Product> orderedProducts, String address, String deliveryDate, boolean isValidateByPlaneur,String state,String driverSelected) {
        this.tempId= tempId;
        this.userEmail = userEmail;
        this.orderedProducts = orderedProducts;
        this.address = address;
        this.deliveryDate = deliveryDate;
        this.state=state;
        this.isValidateByPlaneur=isValidateByPlaneur;
        this.driverSelected=driverSelected;
    }

    public Order(String userEmail, ArrayList<Product> orderedProducts, String address, String deliveryDate, boolean isValidateByPlaneur, String state, String driverSelected) {
        this.userEmail = userEmail;
        this.orderedProducts = orderedProducts;
        this.address = address;
        this.deliveryDate = deliveryDate;
        this.state = state;
        this.isValidateByPlaneur=isValidateByPlaneur;

        this.driverSelected = driverSelected;
    }

    public boolean getIsValidateByPlaneur() {
        return isValidateByPlaneur;
    }

    public void setValidateByPlaneur(boolean validateByPlaneur) {
        isValidateByPlaneur = validateByPlaneur;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public String getDriverSelected() {
        return driverSelected;
    }

    public void setDriverSelected(String driverSelected) {
        this.driverSelected = driverSelected;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
