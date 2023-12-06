package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.livraison.R;
import com.example.livraison.model.Product;
import com.example.livraison.view.adapter.PlanificateurAdapter;
import com.example.livraison.model.Order;
import com.example.livraison.model.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.widget.Toast;

public class PlanificateurHome extends AppCompatActivity {
    RecyclerView recyclerView;
    PlanificateurAdapter planificateurAdapter;
    ArrayList<User> userArrayList = new ArrayList<>();
    ArrayList<Order> orderArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificateur_home);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        recyclerView = findViewById(R.id.order_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planificateurAdapter = new PlanificateurAdapter(this, userArrayList, orderArrayList);
        recyclerView.setAdapter(planificateurAdapter);

        loadOrders();

    }

    private void loadOrders() {
        db.collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, "Error loading products: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            orderArrayList.add(dc.getDocument().toObject(Order.class));
                        }
                    }

                    planificateurAdapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}
