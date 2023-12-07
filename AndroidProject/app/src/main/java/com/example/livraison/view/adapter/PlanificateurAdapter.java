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
    private ArrayList<String> driverEmails;

    public PlanificateurAdapter(Context context, ArrayList<User> userArrayList, ArrayList<Order> orderArrayList,ArrayList<String> driverEmails) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.orderArrayList = orderArrayList;
        this.driverEmails = driverEmails;
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
        holder.userEmail.setText(order.getUserEmail());

        // Configurer le Spinner avec les emails des chauffeurs
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, driverEmails);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerDrivers.setAdapter(spinnerAdapter);

        // Ajouter un écouteur pour gérer la sélection des éléments du Spinner
        holder.spinnerDrivers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Logique à exécuter lorsqu'un élément est sélectionné
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Logique à exécuter lorsqu'aucun élément n'est sélectionné
            }
        });
    }
    // Méthode pour mettre à jour les emails des chauffeurs
    public void setDriverEmails(ArrayList<String> driverEmails) {
        this.driverEmails = driverEmails;
        notifyDataSetChanged(); // Informer l'adapter que les données ont changé
    }
    @Override
    public int getItemCount() {return orderArrayList.size();}
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail;
        TextView deliveryDate;
        TextView deliveryAddress;
        Spinner spinnerDrivers;
        Button submitButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail=itemView.findViewById(R.id.textViewUserEmail);
            deliveryDate = itemView.findViewById(R.id.textViewDeliveryDate);
            deliveryAddress = itemView.findViewById(R.id.textViewAddress);
            spinnerDrivers = itemView.findViewById(R.id.spinnerDrivers);
            submitButton = itemView.findViewById(R.id.submit_delivery_button);
        }
    }

}
