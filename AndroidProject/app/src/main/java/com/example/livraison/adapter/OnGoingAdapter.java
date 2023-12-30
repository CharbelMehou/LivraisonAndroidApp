package com.example.livraison.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.example.livraison.acitvity.ViewItineraire;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;

public class OnGoingAdapter extends RecyclerView.Adapter<OnGoingAdapter.MyViewHolder> {

    private Context context;
    private HashMap<String, ArrayList<Order>> ordersGroupedByDate;
    private ArrayList<String> deliveryDates;
    private String driverEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OnGoingAdapter(Context context, HashMap<String, ArrayList<Order>> ordersGroupedByDate, String driverEmail) {
        this.context = context;
        this.ordersGroupedByDate = ordersGroupedByDate;
        this.deliveryDates = new ArrayList<>(ordersGroupedByDate.keySet());
        this.driverEmail = driverEmail;
    }

    @NonNull
    @Override
    public OnGoingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ongoing_delivery_card, parent, false);
        return new OnGoingAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OnGoingAdapter.MyViewHolder holder, int position) {
        String deliveryDate = deliveryDates.get(position);
        ArrayList<Order> ordersForThisDate = ordersGroupedByDate.get(deliveryDate);

        holder.deliveryDate.setText(deliveryDate);

        OnGoingItemAdapter itemAdapter = new OnGoingItemAdapter(context, ordersForThisDate, driverEmail);
        holder.recyclerViewDeliveryItems.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewDeliveryItems.setAdapter(itemAdapter);

        holder.viewDeliveryItineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> orderIds = new ArrayList<>();
                for (Order order : ordersForThisDate) {
                    orderIds.add(order.getTempId());
                }

                Intent intent = new Intent(context, ViewItineraire.class);
                intent.putStringArrayListExtra("ORDER_ID_LIST", orderIds);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryDates.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryDate;
        Button viewDeliveryItineraire;
        RecyclerView recyclerViewDeliveryItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            viewDeliveryItineraire = itemView.findViewById(R.id.viewDeliveryItineraire);
            recyclerViewDeliveryItems = itemView.findViewById(R.id.recyclerViewDeliveryItems);
        }
    }
}
