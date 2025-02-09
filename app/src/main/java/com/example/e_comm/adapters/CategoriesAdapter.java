package com.example.e_comm.adapters;

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
import com.example.e_comm.clicklistners.RecyclerViewClickListener;
import com.example.e_comm.models.Categories;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Categories> categories;
    private Context context;
    private RecyclerViewClickListener mClickListener;

    public CategoriesAdapter(List<Categories> categories, Context context, RecyclerViewClickListener mClickListener) {
        this.categories = new ArrayList<>(categories);
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindModel(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateList(List<Categories> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Categories mCategory;
        private TextView catTitle;
        private ImageView catIcon;

        ViewHolder(View v) {
            super(v);
            catTitle = itemView.findViewById(R.id.cat_title);
            catIcon = itemView.findViewById(R.id.cat_icon);
            itemView.setOnClickListener(this);
        }

        void bindModel(Categories category) {
            this.mCategory = category;
            catTitle.setText(category.getName());

            if (category.getIcon() != null && !category.getIcon().isEmpty()) {
                Glide.with(context)
                        .load(category.getIcon())
                        .into(catIcon);
            }
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, mCategory);
        }
    }
}
