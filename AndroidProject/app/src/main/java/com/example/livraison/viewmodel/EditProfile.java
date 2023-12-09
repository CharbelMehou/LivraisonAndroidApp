package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword, editTextPhone, editTextTruckNumber, editTextAddress;
    private TextInputLayout truckNumberLayout, addressLayout;
    private Button buttonSave,buttonBa;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private  String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialisation des composants
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextTruckNumber = findViewById(R.id.truckNumberEditText);
        editTextAddress = findViewById(R.id.adressEditText);
        truckNumberLayout = findViewById(R.id.truckNumberLayout);
        addressLayout = findViewById(R.id.addressLayout);
        buttonSave = findViewById(R.id.buttonSave);
        Button goBackButton=findViewById(R.id.goback_button);

        // Récupération et gestion du rôle de l'utilisateur
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            userRole=role;
                            // Afficher/Cacher les champs en fonction du rôle
                            if ("chauffeur".equals(role)) {
                                truckNumberLayout.setVisibility(View.VISIBLE);
                            } else if ("client".equals(role)) {
                                addressLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfile.this, "Failed to get user data.", Toast.LENGTH_SHORT).show();
                    });
        }

        // Bouton pour enregistrer les modifications
        buttonSave.setOnClickListener(v -> {
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String truckNumber = editTextTruckNumber.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();

                // Mettre à jour l'email et le mot de passe dans Firebase Auth
                if (!TextUtils.isEmpty(email)) {
                    currentUser.updateEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditProfile.this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfile.this, "Failed to update email!" , Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                if (!TextUtils.isEmpty(password)) {
                    currentUser.updatePassword(password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditProfile.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfile.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                // Préparer les données pour la mise à jour dans Firestore
                Map<String, Object> updates = new HashMap<>();
                if (!phone.isEmpty()) updates.put("phoneNumber", phone);
                if (!truckNumber.isEmpty()) updates.put("immatriculation", truckNumber);
                if (!address.isEmpty()) updates.put("address", address);

                // Mettre à jour les informations dans Firestore
                db.collection("users").document(userId)
                        .update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfile.this, "User data updated successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditProfile.this, "Failed to update data!", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(EditProfile.this, "No user signed in!", Toast.LENGTH_SHORT).show();
            }
        });


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (userRole) {
                    case "client":
                        intent = new Intent(getApplicationContext(), ClientHome.class);
                        break;
                    case "planificateur":
                        intent = new Intent(getApplicationContext(), PlanificateurHome.class);
                        break;
                    case "chauffeur":
                        intent = new Intent(getApplicationContext(), ChauffeurHome.class);
                        break;
                    default:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                }
                startActivity(intent);
                finish();
            }
        });

    }
}