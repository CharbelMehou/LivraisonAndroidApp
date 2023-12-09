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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class WaintingDelivery extends AppCompatActivity {

    private RecyclerView deliveryRecyclerView;
    private DriverAdapter driverAdapter;
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private String driverEmail; // Variable to store driver's email
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
            driverEmail = user.getEmail(); // Store the email in the variable
        }

        // Set up the RecyclerView
        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
        deliveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        driverAdapter = new DriverAdapter(this, orderArrayList, driverEmail);
        deliveryRecyclerView.setAdapter(driverAdapter);

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
                // Use appropriate query to filter the orders for the logged-in driver
                // For example, to get only orders that are waiting and validated by the planner:
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

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());

                            switch (dc.getType()) {
                                case ADDED:
                                    // Assuming you want to add to the list only if the order is waiting and validated
                                    if (order.getIsValidateByPlaneur() && "waiting".equals(order.getState())) {
                                        orderArrayList.add(order);
                                    }
                                    break;
                                case MODIFIED:
                                    // Here, find the existing Order and update or remove it
                                    for (int i = 0; i < orderArrayList.size(); i++) {
                                        if (orderArrayList.get(i).getTempId().equals(order.getTempId())) {
                                            if (order.getIsValidateByPlaneur() && "waiting".equals(order.getState())) {
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

                        driverAdapter.notifyDataSetChanged();
                    }

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }


}
