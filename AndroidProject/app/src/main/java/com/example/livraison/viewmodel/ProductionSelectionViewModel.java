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
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productArrayList, this);

        productsRecyclerView = findViewById(R.id.products_recycler_view);
        productsRecyclerView.setHasFixedSize(true);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        loadProducts();

        Button submitOrderButton = findViewById(R.id.submit_order_button);
        submitOrderButton.setOnClickListener(v -> showBottomSheetDialog());

        Button goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductionSelectionViewModel.this, ClientHome.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadProducts() {
        db.collection("produit")
                .orderBy("nom", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
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
                    progressDialog.dismiss();
                });
    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_delivery_data);

        Button buttonConfirm = bottomSheetDialog.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(v -> {
            EditText editTextAddressDialog = bottomSheetDialog.findViewById(R.id.editTextAddressDialog);
            EditText editTextDateDialog = bottomSheetDialog.findViewById(R.id.editTextDateDialog);

            String address = editTextAddressDialog.getText().toString();
            String deliveryDate = editTextDateDialog.getText().toString();

            if (address.isEmpty() || deliveryDate.isEmpty()) {
                Toast.makeText(this, "Address and date are required", Toast.LENGTH_SHORT).show();
            } else {
                placeOrder(address, deliveryDate);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void placeOrder(String address, String deliveryDate) {
        ArrayList<Product> selectedProducts = getSelectedProducts();
        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "No products selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Order newOrder = new Order(userEmail, selectedProducts, address, deliveryDate, false, "waiting", "none");

        db.collection("orders").add(newOrder)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to place order.", Toast.LENGTH_LONG).show());
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
