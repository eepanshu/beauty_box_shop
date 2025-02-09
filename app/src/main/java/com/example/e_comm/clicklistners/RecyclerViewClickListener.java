package com.example.e_comm.clicklistners;

import android.view.View;

import com.example.e_comm.models.Categories;

public interface RecyclerViewClickListener {
    abstract void onClick(View view, Categories categories);
}
