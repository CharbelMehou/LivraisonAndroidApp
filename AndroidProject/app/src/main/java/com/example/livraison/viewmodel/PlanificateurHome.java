package com.example.livraison.viewmodel;

import static android.widget.Toast.LENGTH_SHORT;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PlanificateurHome extends AppCompatActivity {
    FirebaseAuth auth;
    Button logOutbutton,setupDriverButton,setupItineraireButton;
    Button buttonSetData;
    TextView textView;
    FirebaseUser user;

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
        setupDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),SetupDriver.class);
                startActivity(intent);
                finish();
            }
        });
        setupItineraireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),SetupItineraire.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
