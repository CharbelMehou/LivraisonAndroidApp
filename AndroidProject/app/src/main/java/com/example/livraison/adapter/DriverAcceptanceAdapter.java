package com.example.livraison.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverAcceptanceAdapter extends RecyclerView.Adapter<DriverAcceptanceAdapter.MyViewHolder> {

    private Context context;
    private HashMap<String, ArrayList<Order>> ordersGroupedByDate;
    private ArrayList<String> deliveryDates;
    private String driverEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DriverAcceptanceAdapter(Context context, HashMap<String, ArrayList<Order>> ordersGroupedByDate, String driverEmail) {
        this.context = context;
        this.ordersGroupedByDate = ordersGroupedByDate;
        this.deliveryDates = new ArrayList<>(ordersGroupedByDate.keySet());
        this.driverEmail = driverEmail;
    }

    @NonNull
    @Override
    public DriverAcceptanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.delivery_acceptance_card, parent, false);
        return new DriverAcceptanceAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverAcceptanceAdapter.MyViewHolder holder, int position) {
        String deliveryDate = deliveryDates.get(position);
        ArrayList<Order> ordersForThisDate = ordersGroupedByDate.get(deliveryDate);

        holder.deliveryDate.setText(deliveryDate);

        DriverAcceptanceItemAdapter itemAdapter = new DriverAcceptanceItemAdapter(context, ordersForThisDate, driverEmail);
        holder.recyclerViewDeliveryItems.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewDeliveryItems.setAdapter(itemAdapter);

        holder.ok_delivery_button.setOnClickListener(v -> {
            ArrayList<Order> toRemove = new ArrayList<>(ordersForThisDate);
            for (Order order : toRemove) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("state", "accepted");
                updates.put("isValidateByPlaneur", true);
                updateOrderFirestore(order.getTempId(), updates, true);
            }
            // Après la mise à jour de Firestore, supprimer les commandes acceptées
            ordersForThisDate.removeAll(toRemove);
            notifyDataSetChanged();
        });

        holder.cancel_delivery_button.setOnClickListener(v -> {
            // Créer une copie temporaire pour éviter les modifications concurrentes
            ArrayList<Order> toRemove = new ArrayList<>(ordersForThisDate);
            for (Order order : toRemove) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("driverSelected", null);
                updates.put("isValidateByPlaneur", false);
                updateOrderFirestore(order.getTempId(), updates, false);
            }
            // Après la mise à jour de Firestore, supprimer les commandes refusées
            ordersForThisDate.removeAll(toRemove);
            notifyDataSetChanged();
        });
    }

    private void updateOrderFirestore(String orderId, Map<String, Object> updates, boolean isAccepted) {
        db.collection("orders").document(orderId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    String message;
                    if (isAccepted) {
                        message = "Mission acceptée avec succès";
                    } else {
                        message = "Mission refusée avec succès";
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("DriverAdapter", "Échec de la mise à jour de la commande: " + e.getMessage());
                    Toast.makeText(context, "Échec de la mise à jour de la mission", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return deliveryDates.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryDate;
        Button ok_delivery_button, cancel_delivery_button;
        RecyclerView recyclerViewDeliveryItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            ok_delivery_button = itemView.findViewById(R.id.ok_delivery_button);
            cancel_delivery_button = itemView.findViewById(R.id.cancel_delivery_button);
            recyclerViewDeliveryItems = itemView.findViewById(R.id.recyclerViewDeliveryItems);
        }
    }
}
