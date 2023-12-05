package com.example.livraison.repository;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductRepository {
    private FirebaseFirestore db;

    public ProductRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getProducts(EventListener<QuerySnapshot> listener) {
        db.collection("produit")
                .orderBy("nom", Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }

}
