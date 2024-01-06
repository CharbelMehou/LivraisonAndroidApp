package com.example.livraison.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DriverAcceptanceItemAdapter extends RecyclerView.Adapter<DriverAcceptanceItemAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Order> orderArrayList;
    private String driverEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DriverAcceptanceItemAdapter(Context context, ArrayList<Order> orderArrayList, String driverEmail) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.driverEmail = driverEmail;
    }

    @NonNull
    @Override
    public DriverAcceptanceItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.delivery_acceptance_card_item, parent, false);
        return new DriverAcceptanceItemAdapter.MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull DriverAcceptanceItemAdapter.MyViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        holder.deliveryAddress.setText(order.getAddress());
        holder.userEmail.setText(order.getUserEmail());
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, deliveryAddress;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
        }
    }


}
