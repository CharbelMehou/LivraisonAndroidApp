package com.example.livraison.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Product> productArrayList;

    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.nomProduct.setText(product.getNom());
        holder.quantityProduct.setText(product.getQuantity());
    }
    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomProduct, quantityProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomProduct = itemView.findViewById(R.id.textViewNomProduct);
            quantityProduct = itemView.findViewById(R.id.textViewQuantityProduct);
        }
    }
}