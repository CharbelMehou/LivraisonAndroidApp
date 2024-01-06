package com.example.livraison.acitvity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.adapter.OnGoingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class OnGoingDelivery extends AppCompatActivity {
    private RecyclerView deliveryOngoingRecyclerView;
    private OnGoingAdapter onGoingAdapter;
    private ProgressDialog progressDialog;
    private Button goBackButton;
    private String driverEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Structure pour regrouper les commandes par date
    private HashMap<String, ArrayList<Order>> ordersGroupedByDate = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going_delivery);

        // Initialiser la ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // Obtenir l'email du conducteur actuel depuis Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            driverEmail = user.getEmail();
        }

        // Configurer le RecyclerView
        deliveryOngoingRecyclerView = findViewById(R.id.delivery_ongoing_recycler_view);
        deliveryOngoingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurer le bouton Go Back
        goBackButton = findViewById(R.id.goback_button);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChauffeurHome.class);
            startActivity(intent);
            finish();
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

                    // Réinitialiser la structure pour regrouper les commandes par date
                    ordersGroupedByDate.clear();

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setTempId(dc.getDocument().getId());

                            // Ajoutez les commandes dans le HashMap groupé par date
                            String deliveryDate = order.getDeliveryDate(); // Assurez-vous que cette méthode renvoie la date sous forme de String
                            ArrayList<Order> ordersForDate = ordersGroupedByDate.getOrDefault(deliveryDate, new ArrayList<>());
                            ordersForDate.add(order);
                            ordersGroupedByDate.put(deliveryDate, ordersForDate);
                        }

                        // Mettez à jour l'adaptateur avec les données groupées et informez le RecyclerView de la mise à jour
                        onGoingAdapter = new OnGoingAdapter(this, ordersGroupedByDate, driverEmail);
                        deliveryOngoingRecyclerView.setAdapter(onGoingAdapter);
                    }

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}