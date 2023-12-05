package com.example.livraison.repository;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.livraison.model.Product;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
