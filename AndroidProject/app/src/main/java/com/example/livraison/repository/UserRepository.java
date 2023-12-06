package com.example.livraison.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void getUserRole(OnUserRoleReceivedListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            listener.onRoleReceived(role);
                        } else {
                            listener.onRoleReceived(null);
                        }
                    })
                    .addOnFailureListener(e -> listener.onRoleReceived(null));
        } else {
            listener.onRoleReceived(null);
        }
    }

    public interface OnUserRoleReceivedListener {
        void onRoleReceived(String role);
    }
}
