package com.example.livraison.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlanificateurAdapter extends RecyclerView.Adapter<PlanificateurAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Order> orderArrayList;
    private ArrayList<String> driverEmails;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PlanificateurAdapter(Context context, ArrayList<Order> orderArrayList, ArrayList<String> driverEmails) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.driverEmails = driverEmails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.order_to_driver_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orderArrayList.get(position);

        if (order.getIsValidateByPlaneur()) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        holder.deliveryAddress.setText(order.getAddress());
        holder.deliveryDate.setText(order.getDeliveryDate());
        holder.userEmail.setText(order.getUserEmail());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, driverEmails);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerDrivers.setAdapter(spinnerAdapter);

        holder.submitButton.setOnClickListener(v -> {
            int spinnerPosition = holder.spinnerDrivers.getSelectedItemPosition();
            if (spinnerPosition != AdapterView.INVALID_POSITION) {
                String selectedDriverEmail = driverEmails.get(spinnerPosition);
                String orderId = order.getTempId();
                if (orderId != null) {
                    updateDriverSelected(orderId, selectedDriverEmail, holder);
                } else {
                    Log.e("PlanificateurAdapter", "Cannot update order because orderId is null.");
                }
            } else {
                Log.e("PlanificateurAdapter", "No driver selected when trying to validate delivery.");
            }
        });
    }

    private void updateDriverSelected(String orderId, String driverEmail, MyViewHolder holder) {
        db.collection("orders").document(orderId)
                .update("driverSelected", driverEmail)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Order validated successfully with driver: " + driverEmail, Toast.LENGTH_SHORT).show();
                    // Here you would update the 'isValidateByPlaneur' flag as well
                    updateIsValidateByPlaneur(orderId, true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to validate order with driver: " + driverEmail, Toast.LENGTH_SHORT).show();
                    Log.e("Update", "Error updating driver selected for order: " + orderId, e);
                });
    }

    private void updateIsValidateByPlaneur(String orderId, boolean isValidateByPlaneur) {
        db.collection("orders").document(orderId)
                .update("isValidateByPlaneur", isValidateByPlaneur)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Update", "Order is validate by the Planneur");
                    // After successful update, you may want to remove the order from the list and update the adapter
                    removeOrderFromList(orderId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Update", "Order is not validate by the Planneur: ", e);
                });
    }

    private void removeOrderFromList(String orderId) {
        for (int i = 0; i < orderArrayList.size(); i++) {
            if (orderArrayList.get(i).getTempId().equals(orderId)) {
                orderArrayList.remove(i);
                break;
            }
        }
        notifyDataSetChanged(); // To update the RecyclerView
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public void setDriverEmails(ArrayList<String> driverEmails) {
        this.driverEmails = driverEmails;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, deliveryDate, deliveryAddress;
        Spinner spinnerDrivers;
        Button submitButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
            spinnerDrivers = itemView.findViewById(R.id.spinnerDrivers);
            submitButton = itemView.findViewById(R.id.submit_delivery_button);
        }
    }
}
