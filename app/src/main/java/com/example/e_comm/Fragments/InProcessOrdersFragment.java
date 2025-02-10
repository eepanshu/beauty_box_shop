package com.example.e_comm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.e_comm.Helpers.DataStatus;
import com.example.e_comm.Helpers.Singleton;
import com.example.e_comm.R;
import com.example.e_comm.adapters.UserOrderAdapter;

import com.example.e_comm.databinding.FragmentInProcessOrdersBinding;
import com.example.e_comm.models.Order;
import com.example.e_comm.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class InProcessOrdersFragment extends Fragment {

    private static final String TAG = "InProcessOrders";
    private FirebaseFirestore db = Singleton.getDb();
    private UserOrderAdapter adapter;
    private ArrayList<Order> ordersList = new ArrayList<>();
    private FirebaseUser user = Singleton.getUser();
    private Product product;
    private FragmentInProcessOrdersBinding binding; // Data Binding

    public InProcessOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize Data Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_in_process_orders, container, false);

        // Set up RecyclerView
        binding.usersOrdersRcInprocess.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersOrdersRcInprocess.setHasFixedSize(true);

        getProducts(new DataStatus() {
            @Override
            public void onSuccess(ArrayList list) {
                if (adapter == null) {
                    adapter = new UserOrderAdapter(list, getContext());
                    binding.usersOrdersRcInprocess.setAdapter(adapter);
                } else {
                    adapter.getItems().clear();
                    adapter.getItems().addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String e) {
                Log.e(TAG, "Error fetching data: " + e);
            }
        });

        return binding.getRoot();
    }

    private void getProducts(final DataStatus callback) {
        db.collection("orders").whereEqualTo("status", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.v("userIdd", user.getUid() + " order user ID:" + document.get("id_user"));

                                if (user.getUid().matches(document.get("id_user").toString())) {
                                    ArrayList<Map> productsinorder = (ArrayList<Map>) document.get("products");
                                    ArrayList<Product> productArrayList = new ArrayList<>();

                                    if (productsinorder != null) {
                                        for (Map myMap : productsinorder) {
                                            product = new Product();
                                            product.setPrice((Double) myMap.get("price"));
                                            product.setImg_product(myMap.get("img_product").toString());
                                            product.setNom(myMap.get("nom").toString());
                                            product.setId_seller(myMap.get("id_seller").toString());
                                            product.setDescription(myMap.get("description").toString());
                                            product.setId_cat(myMap.get("id_cat").toString());

                                            productArrayList.add(product);
                                        }
                                    }

                                    Order order = new Order(
                                            document.getId(),
                                            productArrayList,
                                            (String) document.get("id_user"),
                                            (Long) document.get("status"));
                                    ordersList.add(order);
                                }
                            }
                            callback.onSuccess(ordersList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
