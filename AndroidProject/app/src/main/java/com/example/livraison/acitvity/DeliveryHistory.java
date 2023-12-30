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
import com.example.livraison.model.Order;
import com.example.livraison.adapter.DeliveryHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DeliveryHistory extends AppCompatActivity {


    private RecyclerView historyDeliveryRecyclerView;
    private DeliveryHistoryAdapter deliveryHistoryAdapter;
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private String driverEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);

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
        historyDeliveryRecyclerView = findViewById(R.id.delivery_history_recycler_view);
        historyDeliveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deliveryHistoryAdapter = new DeliveryHistoryAdapter(this, orderArrayList, driverEmail);
        historyDeliveryRecyclerView.setAdapter(deliveryHistoryAdapter);

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
                .whereEqualTo("state", "delivered")
                .whereEqualTo("driverSelected", driverEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("DeliveryHistory", "Error loading orders", error);
                        Toast.makeText(this, "Error loading orders: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());

                            switch (dc.getType()) {
                                case ADDED:
                                case MODIFIED:
                                    orderArrayList.add(order);
                                    break;
                                case REMOVED:
                                    orderArrayList.removeIf(o -> o.getTempId().equals(order.getTempId()));
                                    break;
                            }
                        }

                        deliveryHistoryAdapter.notifyDataSetChanged();
                    }

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }

}