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
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Order> orderArrayList;
    private String driverEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DriverAdapter(Context context, ArrayList<Order> orderArrayList, String driverEmail) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.driverEmail = driverEmail;
    }

    @NonNull
    @Override
    public DriverAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.delivery_card, parent, false);
        return new DriverAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverAdapter.MyViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        holder.deliveryAddress.setText(order.getAddress());
        holder.deliveryDate.setText(order.getDeliveryDate());
        holder.userEmail.setText(order.getUserEmail());

        if (order.getIsValidateByPlaneur() && order.getState().equals("waiting") && order.getDriverSelected().equals(driverEmail)) {
            holder.ok_delivery_button.setVisibility(View.VISIBLE);
            holder.cancel_delivery_button.setVisibility(View.VISIBLE);
        } else {
            holder.ok_delivery_button.setVisibility(View.GONE);
            holder.cancel_delivery_button.setVisibility(View.GONE);
        }

        holder.ok_delivery_button.setOnClickListener(v -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("state", "accepted");
            updates.put("isValidateByPlaneur", true);
            updateOrderFirestore(order.getTempId(), updates,true);
            removeOrderFromList(order.getTempId());
        });

        holder.cancel_delivery_button.setOnClickListener(v -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("driverSelected", null);
            updates.put("isValidateByPlaneur", false);
            updateOrderFirestore(order.getTempId(), updates,false);
        });
    }

    private void updateOrderFirestore(String orderId, Map<String, Object> updates,boolean isAccepted) {
        db.collection("orders").document(orderId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    String message;
                    if(isAccepted==true){
                        message="Commande validée avec succès";}
                    else{
                        message="Commande refusée avec succès";}
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("DriverAdapter", "Échec de la mise à jour de la commande: " + e.getMessage());
                    Toast.makeText(context, "Échec de la mise à jour de la commande", Toast.LENGTH_SHORT).show();
                });
    }

    private void removeOrderFromList(String orderId) {
        for (int i = 0; i < orderArrayList.size(); i++) {
            if (orderArrayList.get(i).getTempId().equals(orderId)) {
                orderArrayList.remove(i);
                break;
            }
        }
        notifyDataSetChanged(); // Pour mettre à jour le RecyclerView
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, deliveryDate, deliveryAddress;
        Button ok_delivery_button, cancel_delivery_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
            ok_delivery_button = itemView.findViewById(R.id.ok_delivery_button);
            cancel_delivery_button = itemView.findViewById(R.id.cancel_delivery_button);
        }
    }
}
