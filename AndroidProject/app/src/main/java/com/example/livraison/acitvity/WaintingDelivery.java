package com.example.livraison.acitvity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.example.livraison.adapter.DriverAcceptanceAdapter;
import com.example.livraison.adapter.OnGoingAdapter;
import com.example.livraison.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class WaintingDelivery extends AppCompatActivity {

    private RecyclerView deliveryRecyclerView;
    private DriverAcceptanceAdapter driverAcceptanceAdapter;
    private HashMap<String, ArrayList<Order>> ordersGroupedByDate = new HashMap<>();
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private String driverEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wainting_delivery);

        // Initialize the ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // Get the current driver's email from Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            driverEmail = user.getEmail();
        }

        // Set up the RecyclerView
        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
        deliveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        driverAcceptanceAdapter = new DriverAcceptanceAdapter(this, ordersGroupedByDate, driverEmail);
        deliveryRecyclerView.setAdapter(driverAcceptanceAdapter);

        // Set up the Go Back button
        goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),ChauffeurHome.class);
                startActivity(intent);
                finish();
            }
        });
        fetchOrders();
    }
    private void fetchOrders() {
        db.collection("orders")
                .whereEqualTo("isValidateByPlaneur", true)
                .whereEqualTo("state", "waiting")
                .whereEqualTo("driverSelected", driverEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("WaitingDelivery", "Error loading orders", error);
                        Toast.makeText(this, "Error loading orders: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Réinitialiser la structure pour regrouper les commandes par date
                    ordersGroupedByDate.clear();

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());

                            String deliveryDate = order.getDeliveryDate();

                            ArrayList<Order> ordersForDate = ordersGroupedByDate.getOrDefault(deliveryDate, new ArrayList<>());
                            ordersForDate.add(order);
                            ordersGroupedByDate.put(deliveryDate, ordersForDate);

                            // Mettez à jour l'adaptateur avec les données groupées et informez le RecyclerView de la mise à jour
                            driverAcceptanceAdapter = new DriverAcceptanceAdapter(this, ordersGroupedByDate, driverEmail);
                            deliveryRecyclerView.setAdapter(driverAcceptanceAdapter);
                        }

                        driverAcceptanceAdapter.notifyDataSetChanged();
                    }

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}
