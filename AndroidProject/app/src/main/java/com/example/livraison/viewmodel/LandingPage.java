package com.example.livraison.viewmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.livraison.R;

public class LandingPage extends AppCompatActivity {
    Button buttonRegister;
    Button buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        buttonRegister=findViewById(R.id.btn_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), RegisterViewModel.class);
                startActivity(intent);
                finish();
            }
        });
        buttonLogin=findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), LoginViewModel.class);
                startActivity(intent);
                finish();
            }
        });



    }
}