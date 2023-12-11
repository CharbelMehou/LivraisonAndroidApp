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

public class OnGoingAdapter extends RecyclerView.Adapter<OnGoingAdapter.MyViewHolder>  {

    private Context context;
    private ArrayList<Order> orderArrayList;
    private String driverEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OnGoingAdapter(Context context, ArrayList<Order> orderArrayList, String driverEmail) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.driverEmail = driverEmail;
    }
    @NonNull
    @Override
    public OnGoingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ongoing_delivery_card, parent, false);
        return new OnGoingAdapter.MyViewHolder(v);
    }
    public void onBindViewHolder(@NonNull OnGoingAdapter.MyViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        holder.deliveryAddress.setText(order.getAddress());
        holder.deliveryDate.setText(order.getDeliveryDate());
        holder.userEmail.setText(order.getUserEmail());
        holder.viewDeliveryItineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, deliveryDate, deliveryAddress;
        Button viewDeliveryItineraire;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
            viewDeliveryItineraire = itemView.findViewById(R.id.viewDeliveryItineraire);
        }
    }
}
