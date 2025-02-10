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
import com.example.e_comm.clicklistners.RecyclerViewClickListenerProduct;
import com.example.e_comm.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsOrdersAdapter extends RecyclerView.Adapter<ProductsOrdersAdapter.ViewHolder> {

    private List<Product> products;
    private Context context;
    private RecyclerViewClickListenerProduct mClickListener;

    public ProductsOrdersAdapter(List<Product> products, Context context, RecyclerViewClickListenerProduct mClickListener) {
        this.products = new ArrayList<>(products);
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_card_usersorders, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bindModel(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateList(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Product mProduct;
        private ImageView productImg;
        private TextView produitNom;
        private TextView produitPrice;

        ViewHolder(View v) {
            super(v);
            productImg = itemView.findViewById(R.id.product_img);
            produitNom = itemView.findViewById(R.id.product_title);
            produitPrice = itemView.findViewById(R.id.product_price);
            v.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Product product) {
            this.mProduct = product;
            produitNom.setText(mProduct.getNom());
            produitPrice.setText(mProduct.getPrice() + " MAD");

            if (mProduct.getImg_product() != null && !mProduct.getImg_product().isEmpty()) {
                Glide.with(context)
                        .load(mProduct.getImg_product())
                        .into(productImg);
            }
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, mProduct);
        }
    }
}
