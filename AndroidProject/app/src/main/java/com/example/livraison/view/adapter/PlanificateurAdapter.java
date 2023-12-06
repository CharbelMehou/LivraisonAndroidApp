package com.example.livraison.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.model.Product;
import com.example.livraison.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PlanificateurAdapter extends RecyclerView.Adapter<PlanificateurAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<User> userArrayList;
    private ArrayList<Order> orderArrayList;

    public PlanificateurAdapter(Context context, ArrayList<User> userArrayList, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public PlanificateurAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.order_to_driver_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanificateurAdapter.MyViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        holder.deliveryAddress.setText(order.getAddress());
        holder.deliveryDate.setText(order.getDeliveryDate());
    }
    @Override
    public int getItemCount() {return orderArrayList.size();}
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryDate;
        TextView deliveryAddress;
        Spinner spinnerDrivers;
        Button submitButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
            spinnerDrivers = itemView.findViewById(R.id.spinnerDrivers);
            submitButton = itemView.findViewById(R.id.submit_delivery_button);
        }
    }

}
