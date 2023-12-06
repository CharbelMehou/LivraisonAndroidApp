package com.example.livraison.viewmodel;

import static android.widget.Toast.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livraison.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterViewModel extends AppCompatActivity {
    TextInputEditText editTextEmail,editTextPassword;
    Button buttonRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    private TextInputLayout truckNumberLayout;
    TextInputLayout addressLayout;
    private MutableLiveData<List<String>> roles;
    private AutoCompleteTextView autoCompleteTextView;
    private RegisterViewModel viewModel;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public RegisterViewModel() {
        roles = new MutableLiveData<>();
        // Initialisation de la liste des rôles
        roles.setValue(Arrays.asList("Client", "Planificateur", "Chauffeur"));
    }

    public LiveData<List<String>> getRoles() {
        return roles;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_view_model);
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        addressLayout=findViewById(R.id.addressLayout);
        buttonRegister=findViewById(R.id.btn_register);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        // Observer la liste des rôles et la mettre à jour dans le menu déroulant
        truckNumberLayout = findViewById(R.id.truckNumberLayout);
        autoCompleteTextView = findViewById(R.id.role_autocomplete);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Client", "Planificateur", "Chauffeur"}
        );
        autoCompleteTextView.setAdapter(adapter);
            //To set the visiblility of the input related to client and chauffeur
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = (String) parent.getItemAtPosition(position);
                // Vérifiez si le rôle sélectionné est "Chauffeur"
                if ("Chauffeur".equals(selectedRole)) {
                    // Affichez le TextInputLayout pour le numéro d'immatriculation
                    truckNumberLayout.setVisibility(View.VISIBLE);
                } else if ("Client".equals(selectedRole)) {
                    // Affichez le TextInputLayout pour le numéro d'immatriculation
                    addressLayout.setVisibility(View.VISIBLE);
                } else {
                    // Cachez les TextInputLayout si l'utilisateur n'est pas un chauffeur et client
                    truckNumberLayout.setVisibility(View.GONE);
                    addressLayout.setVisibility(View.GONE);
                }
            }
        });

        textView=findViewById(R.id.loginNow);
        //An indent to open the login activity
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), LoginViewModel.class);
                startActivity(intent);
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String email = editTextEmail.getText().toString().trim();
                final String password = editTextPassword.getText().toString().trim();
                final String role = autoCompleteTextView.getText().toString().toLowerCase().trim();
                final String address=addressLayout.getEditText() != null ?addressLayout.getEditText().getText().toString().toLowerCase().trim():null;
                final String truckNumber = truckNumberLayout.getEditText() != null ? truckNumberLayout.getEditText().getText().toString().toLowerCase().trim() : null;

                // To check if the data to register are empty or not
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterViewModel.this,"Enter email", LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterViewModel.this,"Enter password", LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Create a new user with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success,
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();
                                        // Prepare user data
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("email", email);
                                        userData.put("role", role);
                                        //to add immatriculation if the user is a chauffeur
                                        if (role.equals("chauffeur") && truckNumber != null) {
                                            userData.put("immatriculation", truckNumber);
                                        }
                                        //To add adress if the user is a client
                                        if(role.equals("client") && address!=null){
                                         userData.put("address",address) ;
                                        }
                                        // Add a new document with the user's UID
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(userId)
                                                .set(userData)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(RegisterViewModel.this, "Account created with additional info.",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Redirect to login or main activity
                                                    Intent intent = new Intent(getApplicationContext(), LoginViewModel.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(RegisterViewModel.this, "Failed to add additional info.",
                                                            Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterViewModel.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

    }


}