package com.example.e_comm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_comm.R;
import com.example.e_comm.models.Product;


import java.util.ArrayList;

public class ProductOrdersAdapter extends RecyclerView.Adapter<ProductOrdersAdapter.ViewHolder> {

    private final ArrayList<Product> products;
    private final Context context;

    public ProductOrdersAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_usersorders, parent, false);
        return new ViewHolder(view);
    }

    public ArrayList<Product> getItems() {
        return products;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindModel(products.get(position));
    }

    @Override
    public int getItemCount() {
        return (products != null) ? products.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImg;
        private final TextView produitNom;
        private final TextView produitPrice;

        ViewHolder(View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.product_img);
            produitNom = itemView.findViewById(R.id.product_title);
            produitPrice = itemView.findViewById(R.id.product_price);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Product product) {
            if (product != null) {
                produitNom.setText(product.getNom() != null ? product.getNom() : "No Name");
                produitPrice.setText(product.getPrice() != null ? product.getPrice() + " MAD" : "N/A");

                Glide.with(itemView.getContext())
                        .load(product.getImg_product())
                        .into(productImg);
            }
        }
    }
}
