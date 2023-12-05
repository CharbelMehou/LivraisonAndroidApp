package com.example.livraison.repository;

import com.example.livraison.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addUser(User user, OnFirestoreTaskComplete onComplete) {
        db.collection("users").document(user.getUserId()).set(user)
                .addOnSuccessListener(aVoid -> onComplete.onSuccess())
                .addOnFailureListener(e -> onComplete.onFailure(e.getMessage()));
    }

    // Ajoutez d'autres méthodes pour récupérer ou mettre à jour des informations sur les utilisateurs

    public interface OnFirestoreTaskComplete {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
