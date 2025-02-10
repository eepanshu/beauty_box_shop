package com.example.e_comm.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.e_comm.R;
import com.example.e_comm.clicklistners.RecyclerViewClickListenerProduct;
import com.example.e_comm.databinding.ProductCardListBinding;
import com.example.e_comm.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> products;
    private final Context context;
    private final RecyclerViewClickListenerProduct mClickListener;

    public ProductAdapter(List<Product> products, Context context, RecyclerViewClickListenerProduct mClickListener) {
        this.products = new ArrayList<>(products);
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ProductCardListBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_card_list, viewGroup, false);
        return new ViewHolder(binding);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ProductCardListBinding binding;

        ViewHolder(ProductCardListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindModel(Product product) {
            binding.setProduct(product);
            binding.executePendingBindings();


            if (product.getImg() != null && !product.getImg().isEmpty()) {
                String base64Image = String.valueOf(product.getImg().get(0));

                if (base64Image != null && !base64Image.isEmpty()) {
                    String imageDataUrl = "data:image/jpeg;base64," + base64Image;

                    Glide.with(context)
                            .load(imageDataUrl)

                            .into(binding.productImg);
                }
            }

            binding.getRoot().setOnClickListener(v -> mClickListener.onClick(v, product));
        }
    }
}
