package com.example.e_comm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.e_comm.R;
import com.example.e_comm.models.Order;
import com.example.e_comm.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder> {

    private final ArrayList<Order> orders;
    private final Context context;
    private final FirebaseFirestore db;

    public UserOrderAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public UserOrderAdapter(ArrayList<Order> orders, Context context, FirebaseFirestore db) {
        this.orders = orders;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.produit_userorders, viewGroup, false);
        return new ViewHolder(v);
    }

    public ArrayList<Order> getItems() {
        return orders;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindModel(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderNom, produitPrice, orderID;
        private final RecyclerView rc_products;

        ViewHolder(View v) {
            super(v);
            orderNom = itemView.findViewById(R.id.product_title_cart);
            produitPrice = itemView.findViewById(R.id.product_price_cart);
            orderID = itemView.findViewById(R.id.id_order);
            rc_products = itemView.findViewById(R.id.rc_products);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            rc_products.setLayoutManager(layoutManager);
            rc_products.setHasFixedSize(true);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Order order) {
            orderNom.setText(order.getId_order());
            ArrayList<?> mOrderProducts = order.getProducts();

            if (mOrderProducts != null && !mOrderProducts.isEmpty()) {
                ProductOrdersAdapter productAdapter = new ProductOrdersAdapter((ArrayList<Product>) mOrderProducts, context);
                rc_products.setAdapter(productAdapter);
                produitPrice.setText("Number of Products: " + mOrderProducts.size());
            }
        }
    }
}
