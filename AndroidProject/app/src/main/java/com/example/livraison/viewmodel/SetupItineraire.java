package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.view.adapter.SetupItineraireAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SetupItineraire extends AppCompatActivity {
    private RecyclerView setupDeliveryItineraireRecyclerView;
    private SetupItineraireAdapter setupItineraireAdapter;
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private ArrayList<Order> orders = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_itineraire);

        // Initialiser la ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        setupDeliveryItineraireRecyclerView = findViewById(R.id.delivery_recycler_view);
        setupDeliveryItineraireRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurer le bouton Go Back
        goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlanificateurHome.class);
            startActivity(intent);
            finish();
        });

        fetchOrders();
    }

    private void fetchOrders() {
        db.collection("orders")
                .whereEqualTo("state", "waiting")
                .whereEqualTo("step", null)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("SetupItineraire", "Error loading orders", error);
                        Toast.makeText(this, "Error loading orders: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    orders.clear();

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());
                            if (order.getDriverSelected() != null) {
                                orders.add(order);
                            }
                        }

                        // Mettez à jour l'adaptateur et informez le RecyclerView de la mise à jour
                        setupItineraireAdapter = new SetupItineraireAdapter(this, orders);
                        setupDeliveryItineraireRecyclerView.setAdapter(setupItineraireAdapter);
                    }

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}
