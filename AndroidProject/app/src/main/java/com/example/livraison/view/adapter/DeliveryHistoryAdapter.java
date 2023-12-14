package com.example.livraison.view.adapter;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveryHistoryAdapter extends RecyclerView.Adapter<DeliveryHistoryAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Order> orderArrayList;
    private String driverEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DeliveryHistoryAdapter(Context context, ArrayList<Order> orderArrayList, String driverEmail) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.driverEmail = driverEmail;
    }

    @NonNull
    @Override
    public DeliveryHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new DeliveryHistoryAdapter.MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull DeliveryHistoryAdapter.MyViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        holder.deliveryAddress.setText(order.getAddress());
        holder.deliveryDate.setText(order.getDeliveryDate());
        holder.userEmail.setText(order.getUserEmail());
    }
    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, deliveryDate, deliveryAddress;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
        }
    }
}
