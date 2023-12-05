package com.example.livraison.view.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livraison.R;
import com.example.livraison.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Product> productArrayList;
    private OnQuantityChangedListener quantityChangedListener;

    // Interface de callback pour notifier des changements de quantité
    public interface OnQuantityChangedListener {
        void onQuantityChanged(Product product, String newQuantity);
    }

    // Constructeur de l'adaptateur
    public ProductAdapter(Context context, ArrayList<Product> productArrayList, OnQuantityChangedListener listener) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.quantityChangedListener = listener;
    }

    // Méthodes de RecyclerView.Adapter...
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.nomProduct.setText(product.getNom());
        holder.quantityEditText.setText(product.getQuantity());
        holder.quantityEditText.addTextChangedListener(new TextWatcherAdapter(product));
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    // Classe ViewHolder...
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomProduct;
        EditText quantityEditText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomProduct = itemView.findViewById(R.id.textViewNomProduct);
            quantityEditText = itemView.findViewById(R.id.editTextQuantityProduct);
        }
    }

    // Adapter pour TextWatcher avec référence au produit
    private class TextWatcherAdapter implements TextWatcher {
        private Product product;

        public TextWatcherAdapter(Product product) {
            this.product = product;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Pas de besoin d'implémentation ici
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Rien ici non plus
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Notifiez l'activité du changement de quantité ici
            if (quantityChangedListener != null) {
                quantityChangedListener.onQuantityChanged(product, s.toString());
            }
        }
    }
}
