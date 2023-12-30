package com.example.livraison.adapter;

import android.content.Context;
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

public class SetupItineraireAdapter extends RecyclerView.Adapter<SetupItineraireAdapter.MyViewHolder>{

    private Context context;
    private HashMap<String, ArrayList<Order>> ordersGroupedByDriver;
    private ArrayList<String> driverEmails;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public SetupItineraireAdapter(Context context, HashMap<String, ArrayList<Order>> ordersGroupedByDriver, ArrayList<String> driverEmails) {
        this.context = context;
        this.ordersGroupedByDriver = ordersGroupedByDriver;
        this.driverEmails = driverEmails;
    }
    @NonNull
    @Override
    public SetupItineraireAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.driver_mission_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetupItineraireAdapter.MyViewHolder holder, int position) {
        String driverEmail = driverEmails.get(position);
        ArrayList<Order> ordersForThisDriver = ordersGroupedByDriver.get(driverEmail);

        holder.textViewDriver.setText(driverEmail);

        SetupItineraireItemAdapter itemAdapter = new SetupItineraireItemAdapter(context, ordersForThisDriver);
        holder.recyclerViewDeliveryItems.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewDeliveryItems.setAdapter(itemAdapter);

        holder.validateItineraire.setOnClickListener(v -> {
            for (Order order : ordersForThisDriver) {
                String step = order.getStep();
                if (step != null) {
                    db.collection("orders").document(order.getTempId())
                            .update("step", step)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Itinéraire paramétrée", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Gérer l'échec ici
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverEmails.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDriver;
        Button validateItineraire;
        RecyclerView recyclerViewDeliveryItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDriver = itemView.findViewById(R.id.textViewDriver);
            validateItineraire=itemView.findViewById(R.id.validateItineraire);
            recyclerViewDeliveryItems = itemView.findViewById(R.id.recyclerViewDeliveryMissionsItems);
        }
    }

}
