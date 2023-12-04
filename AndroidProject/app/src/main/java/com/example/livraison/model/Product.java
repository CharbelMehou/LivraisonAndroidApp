package com.example.livraison.model;

public class Product {
   String nom,quantity;
    public Product(){

    }
    public Product(String nom, String quantity) {
        this.nom = nom;
        this.quantity = quantity;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
