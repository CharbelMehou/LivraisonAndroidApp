package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.OkHttpClient;
import android.util.Pair;
import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.model.Product;
import com.example.livraison.view.adapter.ProductAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class ProductionSelectionViewModel extends AppCompatActivity implements ProductAdapter.OnQuantityChangedListener {

    private FirebaseFirestore db;
    private RecyclerView productsRecyclerView;
    private ArrayList<Product> productArrayList;
    private ProgressDialog progressDialog;
    private ProductAdapter productAdapter;
    private OkHttpClient client = new OkHttpClient();

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

        EditText editTextDateDialog = bottomSheetDialog.findViewById(R.id.editTextDateDialog);
        editTextDateDialog.setOnClickListener(view -> {
            // Obtention de la date courante
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Création du DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> { // Notez le changement ici, 'view' remplacé par 'datePicker'
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextDateDialog.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });


        Button buttonConfirm = bottomSheetDialog.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(v -> {
            EditText editTextAddressDialog = bottomSheetDialog.findViewById(R.id.editTextAddressDialog);
            String address = editTextAddressDialog.getText().toString();
            String deliveryDate = editTextDateDialog.getText().toString();

            if (address.isEmpty() || deliveryDate.isEmpty()) {
                Toast.makeText(this, "Address and date are required", Toast.LENGTH_SHORT).show();
            } else {
                verifyAddress(address, coords -> {
                    if (coords != null) {
                        placeOrder(address, deliveryDate, coords.first, coords.second);
                        bottomSheetDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Address is invalid", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        bottomSheetDialog.show();
    }

    private void placeOrder(String address, String deliveryDate, String latitude, String longitude) {
        ArrayList<Product> selectedProducts = getSelectedProducts();
        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "No products selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Order newOrder = new Order(
                        userEmail,
                        selectedProducts,
                        address,
                        deliveryDate,
                        false,
                        "waiting",
                        "none",
                        latitude,
                        longitude,
                        null
                        );

            db.collection("orders")
                .add(newOrder)
                .addOnSuccessListener(
                        documentReference -> Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(
                        e -> Toast.makeText(this, "Failed to place order.", Toast.LENGTH_LONG).show());
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

    private void verifyAddress(String address,  Consumer<Pair<String, String>> callback) {
        String apiUrl = "https://api-adresse.data.gouv.fr/search/?q=" + Uri.encode(address);

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ProductionSelectionViewModel.this, "Network error", Toast.LENGTH_SHORT).show();
                    callback.accept(null);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray features = jsonObject.getJSONArray("features");
                        if (features.length() > 0) {
                            JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
                            JSONArray coordinates = geometry.getJSONArray("coordinates");
                            String longitude = String.valueOf(coordinates.getDouble(0));
                            String latitude = String.valueOf(coordinates.getDouble(1));
                            runOnUiThread(() -> {
                                callback.accept(new Pair<>(latitude, longitude));
                            });
                        } else {
                            runOnUiThread(() -> {
                                callback.accept(null);
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(ProductionSelectionViewModel.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            callback.accept(null);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ProductionSelectionViewModel.this, "Error from server", Toast.LENGTH_SHORT).show();
                        callback.accept(null);
                    });
                }
            }
        });
}
}
