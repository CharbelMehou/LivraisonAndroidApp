package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.livraison.R;
import com.example.livraison.model.Product;
import com.example.livraison.view.adapter.PlanificateurAdapter;
import com.example.livraison.model.Order;
import com.example.livraison.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PlanificateurHome extends AppCompatActivity {
    FirebaseAuth auth;
    Button logOutbutton;
    Button buttonSetData;
    TextView textView;
    FirebaseUser user;
    RecyclerView recyclerView;
    PlanificateurAdapter planificateurAdapter;
    ArrayList<User> userArrayList = new ArrayList<>();
    ArrayList<String> driversEmails=new ArrayList<>();
    ArrayList<Order> orderArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificateur_home);

        auth= FirebaseAuth.getInstance();
        logOutbutton=findViewById(R.id.logout);
        buttonSetData=findViewById(R.id.setData);
        textView=findViewById(R.id.user_details);
        user =auth.getCurrentUser();
        //to check if the user is log or not
        if(user==null){
            Intent intent =new Intent(getApplicationContext(),LoginViewModel.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText(user.getEmail());
        }
        //rediriger vers la landing page
        logOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To sign Out the user from firebase
                auth.signOut();
                user = auth.getCurrentUser();
                if(user==null){
                    Intent intent =new Intent(getApplicationContext(),LandingPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //Pour rediriger vers la page de selection de modificaiton de donnée de profil
        buttonSetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),EditProfile.class);
                startActivity(intent);
                finish();
            }
        });



        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        recyclerView = findViewById(R.id.order_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planificateurAdapter = new PlanificateurAdapter(this, userArrayList, orderArrayList,driversEmails);
        recyclerView.setAdapter(planificateurAdapter);

        loadOrders();
        loadDriverEmails();

    }

    private void loadOrders() {
        db.collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, "Error loading products: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            orderArrayList.add(dc.getDocument().toObject(Order.class));
                        }
                    }

                    planificateurAdapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }

    private void loadDriverEmails() {
        db.collection("users")
                .whereEqualTo("role", "chauffeur")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        driversEmails.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            if (email != null) {
                                driversEmails.add(email);
                            }
                        }
                        planificateurAdapter.setDriverEmails(driversEmails);
                    } else {
                        Toast.makeText(this, "Error loading driver emails", Toast.LENGTH_SHORT).show();
                    }
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                });
    }
}
