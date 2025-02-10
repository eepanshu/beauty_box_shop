package com.example.e_comm.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.e_comm.R;

import com.example.e_comm.SellerOrdersActivity;
import com.example.e_comm.clicklistners.RecyclerViewClickListenerOrder;
import com.example.e_comm.models.Order;
import com.example.e_comm.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerOrdersAdapter extends RecyclerView.Adapter<SellerOrdersAdapter.ViewHolder> {

    private final ArrayList<Order> orders;
    private final Context context;
    private final RecyclerViewClickListenerOrder mClickListener;
    private final FirebaseFirestore db;

    public SellerOrdersAdapter(ArrayList<Order> orders, Context context , RecyclerViewClickListenerOrder mClickListener) {
        this.orders = orders;
        this.context = context;
        this.mClickListener = mClickListener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seller_product_orders, viewGroup, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView orderNom, produitPrice, orderID;
        private final RecyclerView rc_products;
        private final Button check, cancel;
        private Order mOrder;

        ViewHolder(View itemView) {
            super(itemView);
            orderNom = itemView.findViewById(R.id.product_title_cart);
            produitPrice = itemView.findViewById(R.id.product_price_cart);
            orderID = itemView.findViewById(R.id.id_order);
            rc_products = itemView.findViewById(R.id.rc_products);
            check = itemView.findViewById(R.id.check);
            cancel = itemView.findViewById(R.id.cancel);

            rc_products.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            rc_products.setHasFixedSize(true);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Order order) {
            this.mOrder = order;
            orderNom.setText(order.getId_order());
            orderID.setText("Order ID: " + order.getId_order());

            ArrayList<Product> mOrderProducts = new ArrayList<>();
            for (Object obj : order.getProducts()) {
                if (obj instanceof Product) {
                    mOrderProducts.add((Product) obj);
                }
            }
            ProductOrdersAdapter productAdapter = new ProductOrdersAdapter(mOrderProducts, context );

            rc_products.setAdapter(productAdapter);

            check.setOnClickListener(v -> updateState(1, order.getId_order()));
            cancel.setOnClickListener(v -> updateState(2, order.getId_order()));
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null && mOrder != null) {
                mClickListener.onClick(view, mOrder);
            }
        }
    }

    private void updateState(int status, String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(context, "Invalid Order ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("status", status);

        db.collection("orders").document(orderId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Order Updated Successfully", Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    context.startActivity(new Intent(context, SellerOrdersActivity.class));
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error Updating Order", Toast.LENGTH_SHORT).show());
    }
}

