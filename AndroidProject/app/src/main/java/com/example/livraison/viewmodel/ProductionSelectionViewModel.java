package com.example.livraison.viewmodel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.model.Product;
import com.example.livraison.view.adapter.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductionSelectionViewModel extends AppCompatActivity implements ProductAdapter.OnQuantityChangedListener {

    private FirebaseFirestore db;
    private RecyclerView productsRecyclerView;
    private ArrayList<Product> productArrayList;
    private ProgressDialog progressDialog;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_selection_view_model);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        productsRecyclerView = findViewById(R.id.products_recycler_view);
        productsRecyclerView.setHasFixedSize(true);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productArrayList, this);
        productsRecyclerView.setAdapter(productAdapter);

        EventChangeListener();

        Button submitOrderButton = findViewById(R.id.submit_order_button);
        submitOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Product> selectedProducts = new ArrayList<>();
                for (Product product : productArrayList) {
                    if (product.getQuantity() != null && !product.getQuantity().isEmpty()) {
                        selectedProducts.add(product);
                    }
                }

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String address = "User address here"; // N'oublie pas de remplacer par l'adresse réelle
                String deliveryDate = "Delivery date here"; // Remplacer par la date réelle

                Order newOrder = new Order(userId, selectedProducts, address, deliveryDate);

                db.collection("orders").add(newOrder)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(ProductionSelectionViewModel.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProductionSelectionViewModel.this, "Failed to place order.", Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    private void EventChangeListener() {
        db.collection("produit")
                .orderBy("nom", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                productArrayList.add(dc.getDocument().toObject(Product.class));
                            }
                            productAdapter.notifyDataSetChanged();
                        }

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onQuantityChanged(Product product, String newQuantity) {
        // Mettre à jour la quantité du produit
        product.setQuantity(newQuantity);
    }
}
