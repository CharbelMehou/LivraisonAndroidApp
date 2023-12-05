package com.example.livraison.repository;

import com.example.livraison.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderRepository {
    private FirebaseFirestore db;

    public OrderRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addOrder(Order order, OnFirestoreTaskComplete onComplete) {
        db.collection("orders").add(order)
                .addOnSuccessListener(documentReference -> onComplete.onSuccess())
                .addOnFailureListener(e -> onComplete.onFailure(e.getMessage()));
    }

    public interface OnFirestoreTaskComplete {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
