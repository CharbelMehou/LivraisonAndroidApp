package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.view.adapter.PlanificateurAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SetupDriver extends AppCompatActivity {
    TextView textView;
    FirebaseUser user;
    RecyclerView recyclerView;
    private Button goBackButton;

    PlanificateurAdapter planificateurAdapter;
    ArrayList<String> driversEmails=new ArrayList<>();
    ArrayList<Order> orderArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_driver);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        recyclerView = findViewById(R.id.order_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planificateurAdapter = new PlanificateurAdapter(this, orderArrayList,driversEmails);
        recyclerView.setAdapter(planificateurAdapter);

        // Configurer le bouton Go Back
        goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlanificateurHome.class);
            startActivity(intent);
            finish();
        });
        loadOrders();
        loadDriverEmails();
    }
    private void loadOrders() {
        db.collection("orders")
                .whereEqualTo("isValidateByPlaneur", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, "Error loading orders: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Order order = dc.getDocument().toObject(Order.class);
                        order.setTempId(dc.getDocument().getId());
                        switch (dc.getType()) {
                            case ADDED:
                                if (!order.getIsValidateByPlaneur() && !orderArrayList.contains(order)) {
                                    orderArrayList.add(order);
                                }
                                break;
                            case MODIFIED:
                                for (int i = 0; i < orderArrayList.size(); i++) {
                                    if (orderArrayList.get(i).getTempId().equals(order.getTempId())) {
                                        if (order.getIsValidateByPlaneur()) {
                                            orderArrayList.remove(i);
                                        } else {
                                            orderArrayList.set(i, order);
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

                    planificateurAdapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }



    private void loadDriverEmails() {
        db.collection("users")
                .whereEqualTo("role", "chauffeur")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        driversEmails.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            if (email != null) {
                                driversEmails.add(email);
                            }
                        }
                        planificateurAdapter.setDriverEmails(driversEmails);
                    } else {
                        Toast.makeText(this, "Error loading driver emails", Toast.LENGTH_SHORT).show();
                    }
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }


}