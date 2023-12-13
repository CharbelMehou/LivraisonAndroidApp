package com.example.livraison.viewmodel;

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
import com.example.livraison.model.Order;
import com.example.livraison.view.adapter.DriverAdapter;
import com.example.livraison.view.adapter.OnGoingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OnGoingDelivery extends AppCompatActivity {
    private RecyclerView deliveryOngoingRecyclerView;
    private OnGoingAdapter onGoingAdapter;
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private Button viewDeliveryItineraire;

    private String driverEmail; // Variable to store driver's email
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going_delivery);



        // Initialize the ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // Get the current driver's email from Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            driverEmail = user.getEmail(); // Store the email in the variable
        }

        // Set up the RecyclerView
        deliveryOngoingRecyclerView = findViewById(R.id.delivery_ongoing_recycler_view);
        deliveryOngoingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        onGoingAdapter = new OnGoingAdapter(this, orderArrayList, driverEmail);
        deliveryOngoingRecyclerView.setAdapter(onGoingAdapter);

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
                .whereEqualTo("state", "accepted")
                .whereEqualTo("driverSelected", driverEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("WaitingDelivery", "Error loading orders", error);
                        Toast.makeText(this, "Error loading orders: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());

                            switch (dc.getType()) {
                                case ADDED:
                                    // Assuming you want to add to the list only if the order is waiting and validated
                                    if ("accepted".equals(order.getState())) {
                                        orderArrayList.add(order);
                                    }
                                    break;
                                case MODIFIED:
                                    // Here, find the existing Order and update or remove it
                                    for (int i = 0; i < orderArrayList.size(); i++) {
                                        if (orderArrayList.get(i).getTempId().equals(order.getTempId())) {
                                            if ("accepted".equals(order.getState())) {
                                                orderArrayList.set(i, order);
                                            } else {
                                                orderArrayList.remove(i);
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    orderArrayList.removeIf(o -> o.getTempId().equals(order.getTempId()));
                                    break;
                            }
                        }

                        onGoingAdapter.notifyDataSetChanged();
                    }

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}