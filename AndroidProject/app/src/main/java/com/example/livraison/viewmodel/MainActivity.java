package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.livraison.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    Button buttonSetData;
    Button buttonSelectProduct;

    TextView textView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();
        button=findViewById(R.id.logout);
        buttonSetData=findViewById(R.id.setData);
        buttonSelectProduct=findViewById(R.id.gotoProductSelection);
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
        button.setOnClickListener(new View.OnClickListener() {
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
        //Pour rediriger vers la page de seleciton de produits

        buttonSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),ProductionSelectionViewModel.class);
                startActivity(intent);
                finish();
            }
        });
    }
}