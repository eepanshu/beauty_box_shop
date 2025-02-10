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
import com.example.e_comm.clicklistners.RecyclerViewClickListenerProduct;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.ViewHolder> {

    private ArrayList<Order> orders;
    private Context context;
    private FirebaseFirestore db;
    private RecyclerViewClickListenerProduct mClickListener;

    // Constructor modified to accept the third argument (click listener)
    public UserOrdersAdapter(ArrayList<Order> orders, Context context, RecyclerViewClickListenerProduct mClickListener) {
        this.orders = orders;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_userorders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindModel(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderNom, produitPrice, orderID;
        private RecyclerView rc_products;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNom = itemView.findViewById(R.id.product_title_cart);
            produitPrice = itemView.findViewById(R.id.product_price_cart);
            orderID = itemView.findViewById(R.id.id_order);
            rc_products = itemView.findViewById(R.id.rc_products);
        }

        @SuppressLint("SetTextI18n")
        public void bindModel(Order order) {
            orderNom.setText(order.getId_order());
            ArrayList<Product> mOrderProducts = order.getProducts();
            produitPrice.setText("Number of Products: " + mOrderProducts.size());

            rc_products.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));

            // Pass the third argument (click listener) to ProductsOrdersAdapter
            rc_products.setAdapter(new ProductOrdersAdapter(mOrderProducts, itemView.getContext()));
        }
    }
}
