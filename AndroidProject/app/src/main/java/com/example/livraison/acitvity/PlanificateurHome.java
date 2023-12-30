package com.example.livraison.acitvity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.livraison.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlanificateurHome extends AppCompatActivity {
    FirebaseAuth auth;
    Button logOutbutton, setupDriverButton, setupItineraireButton;
    Button buttonSetData;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificateur_home);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialisation des composants de l'interface utilisateur
        logOutbutton = findViewById(R.id.logout);
        buttonSetData = findViewById(R.id.setData);
        textView = findViewById(R.id.user_details);
        setupDriverButton = findViewById(R.id.setupDriver);
        setupItineraireButton = findViewById(R.id.setupItineraire);

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        // Définir le listener pour le bouton de déconnexion
        logOutbutton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), LandingPage.class);
            startActivity(intent);
            finish();
        });

        // Définir le listener pour le bouton de modification de données de profil
        buttonSetData.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(intent);
            finish();
        });

        // Définir le listener pour le bouton de configuration du chauffeur
        setupDriverButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SetupDriver.class);
            startActivity(intent);
            finish();
        });

        // Définir le listener pour le bouton de configuration de l'itinéraire
        setupItineraireButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SetupItineraire.class);
            startActivity(intent);
            finish();
        });
    }
}
