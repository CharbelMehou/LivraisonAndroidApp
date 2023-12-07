package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.model.Product;
import com.example.livraison.view.adapter.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

        loadProducts();

        Button submitOrderButton = findViewById(R.id.submit_order_button);
        Button goBackButton=findViewById(R.id.goback_button);
        submitOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Product> selectedProducts = getSelectedProducts();
                if (selectedProducts.isEmpty()) {
                    Toast.makeText(ProductionSelectionViewModel.this, "No products selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText editTextAddress = findViewById(R.id.editTextAddress);
                EditText editTextDate = findViewById(R.id.editTextDate);

                String userEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String address = editTextAddress.getText().toString();
                String deliveryDate = editTextDate.getText().toString();
                //the state of the order is initialize to waiting then after the planificateur proccess and the chauffeur confirmation it'll be turn to "lauch "
                String state="waiting";
                //The driver selected is none at this state and it'll change when the planificateur will affect a chauffeur to a mission
                String driverSelected="none";
                boolean isValidateByPlaneur=false;
                if (address.isEmpty() || deliveryDate.isEmpty()) {
                    Toast.makeText(ProductionSelectionViewModel.this, "Address and date are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Order newOrder = new Order(userEmail, selectedProducts, address, deliveryDate,isValidateByPlaneur,state,driverSelected);

                db.collection("orders").add(newOrder)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(ProductionSelectionViewModel.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProductionSelectionViewModel.this, "Failed to place order.", Toast.LENGTH_LONG).show();
                        });
            }
        });


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),ClientHome.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadProducts() {
        db.collection("produit")
                .orderBy("nom", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, "Error loading products: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            productArrayList.add(dc.getDocument().toObject(Product.class));
                        }
                    }

                    productAdapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }

    private ArrayList<Product> getSelectedProducts() {
        ArrayList<Product> selectedProducts = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product.getQuantity() != null && !product.getQuantity().isEmpty() && !product.getQuantity().equals("0")) {
                selectedProducts.add(product);
            }
        }
        return selectedProducts;
    }

    @Override
    public void onQuantityChanged(Product product, String newQuantity) {
        product.setQuantity(newQuantity);
    }
}
