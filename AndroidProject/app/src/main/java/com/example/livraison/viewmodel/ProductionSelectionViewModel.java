package com.example.livraison.viewmodel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.livraison.R;
import com.example.livraison.model.Product;
import com.example.livraison.view.adapter.ProductAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductionSelectionViewModel extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView productsRecyclerView;
    private ArrayList<Product> productArrayList;
    ProgressDialog progressDialog;
    ProductAdapter productAdapter;

    // Appelé lors de la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_selection_view_model);


        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();


        productsRecyclerView=findViewById(R.id.products_recycler_view);
        productsRecyclerView.setHasFixedSize(true);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db=FirebaseFirestore.getInstance();
        productArrayList=new ArrayList<Product>();
        productAdapter=new ProductAdapter(ProductionSelectionViewModel.this,productArrayList);

        productsRecyclerView.setAdapter(productAdapter);
        EventChangeListener();
    }
    private void EventChangeListener(){
        db.collection("produit")
                .orderBy("nom", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error !=null){
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error",error.getMessage()) ;
                            return;
                        }
                        for(DocumentChange dc:value.getDocumentChanges()){
                            if(dc.getType()==DocumentChange.Type.ADDED){
                                productArrayList.add(dc.getDocument().toObject(Product.class));
                            }
                            productAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }

}


