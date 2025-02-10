package com.example.e_comm.clicklistners;

import android.view.View;

import com.example.e_comm.models.Order;


/**
 * Created by yousra on 04/10/2017.
 */

public interface RecyclerViewClickListenerOrder {
    abstract void onClick(View view, Order product);
}

