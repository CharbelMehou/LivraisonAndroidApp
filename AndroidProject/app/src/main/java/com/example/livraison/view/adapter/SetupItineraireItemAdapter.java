package com.example.livraison.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SetupItineraireItemAdapter extends RecyclerView.Adapter<SetupItineraireItemAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Order> orders;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SetupItineraireItemAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.driver_mission_card_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orders.get(position);

        if (order.getUserEmail() != null && holder.textViewUserAdress != null) {
            holder.textViewUserAdress.setText(order.getAddress());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, generateStepOptions(order));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerDeliveryStep.setAdapter(adapter);

        holder.spinnerDeliveryStep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isInitialSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialSelection) {
                    isInitialSelection = false;
                    return;
                }

                // Mettez à jour l'étape localement sans mise à jour Firestore
                String step = parent.getItemAtPosition(position).toString();
                order.setStep(step);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                isInitialSelection = true;
            }
        });
    }

    private ArrayList<String> generateStepOptions(Order order) {
        int steps = countOrdersWithSameDriver(order.getDriverSelected());
        ArrayList<String> stepOptions = new ArrayList<>();
        for (int i = 0; i <= steps; i++) {
            stepOptions.add(String.valueOf(i));
        }
        return stepOptions;
    }

    private int countOrdersWithSameDriver(String driverEmail) {
        int count = 0;
        for (Order o : orders) {
            if (o.getDriverSelected() != null && o.getDriverSelected().equals(driverEmail)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserAdress;
        Spinner spinnerDeliveryStep;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserAdress = itemView.findViewById(R.id.textViewUserAdress);
            spinnerDeliveryStep = itemView.findViewById(R.id.spinnerDeliveryStep);
        }
    }
}
