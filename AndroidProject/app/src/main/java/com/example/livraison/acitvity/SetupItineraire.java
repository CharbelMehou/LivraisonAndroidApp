package com.example.livraison.acitvity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.adapter.SetupItineraireAdapter;
import com.example.livraison.adapter.SetupItineraireItemAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SetupItineraire extends AppCompatActivity {
    private RecyclerView setupDeliveryItineraireRecyclerView;
    private SetupItineraireItemAdapter setupItineraireItemAdapter;
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private ArrayList<Order> orders = new ArrayList<>();
    private HashMap<String, ArrayList<Order>> ordersGroupedByDriver = new HashMap<>();
    private ArrayList<String> driverEmails = new ArrayList<>();
    private SetupItineraireAdapter setupItineraireAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_itineraire);

        // Initialisation de la ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // Configuration du RecyclerView
        setupDeliveryItineraireRecyclerView = findViewById(R.id.delivery_recycler_view);
        setupDeliveryItineraireRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configuration du bouton Go Back
        goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlanificateurHome.class);
            startActivity(intent);
            finish();
        });

        // Appel de la méthode pour récupérer les commandes
        fetchOrders();
    }

    private void fetchOrders() {
        db.collection("orders")
                .whereEqualTo("state", "waiting")
                .whereEqualTo("step", "null")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Gestion des erreurs
                        return;
                    }

                    // Réinitialisation
                    ordersGroupedByDriver.clear();
                    driverEmails.clear();

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());

                            if (order.getDriverSelected() != null) {
                                ArrayList<Order> ordersForDriver = ordersGroupedByDriver.getOrDefault(order.getDriverSelected(), new ArrayList<>());
                                ordersForDriver.add(order);
                                ordersGroupedByDriver.put(order.getDriverSelected(), ordersForDriver);

                                if (!driverEmails.contains(order.getDriverSelected())) {
                                    driverEmails.add(order.getDriverSelected());
                                }
                            }
                        }

                        // Mise à jour de l'adaptateur principal
                        setupItineraireAdapter = new SetupItineraireAdapter(this, ordersGroupedByDriver, driverEmails);
                        setupDeliveryItineraireRecyclerView.setAdapter(setupItineraireAdapter);
                    }

                    // Fermeture de la ProgressDialog
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}
