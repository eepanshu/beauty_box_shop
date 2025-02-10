package com.example.e_comm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.e_comm.Helpers.Singleton;
import com.example.e_comm.adapters.UserOrderAdapter;
import com.example.e_comm.databinding.FragmentInShippedOrdersBinding;
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

public class InShippedOrdersFragment extends Fragment {

    private FragmentInShippedOrdersBinding binding;
    private static final String TAG = "InShippedOrdersFragment";

    private FirebaseFirestore db = Singleton.getDb();
    private FirebaseUser user = Singleton.getUser();
    private UserOrderAdapter adapter;
    private ArrayList<Order> ordersList = new ArrayList<>();

    public InShippedOrdersFragment() {
        // Required empty public constructor
    }

    public static InShippedOrdersFragment newInstance() {
        return new InShippedOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInShippedOrdersBinding.inflate(inflater, container, false);

        setupRecyclerView();
        getOrders(); // Fetch orders when fragment is created

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.usersOrdersRc.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersOrdersRc.setHasFixedSize(true);
    }

    private void getOrders() {
        db.collection("orders").whereEqualTo("status", 1) // Fetch only shipped orders
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            ordersList.clear(); // Clear existing data

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (user.getUid().equals(document.getString("id_user"))) {

                                    ArrayList<Map<String, Object>> productsInOrder = (ArrayList<Map<String, Object>>) document.get("products");
                                    ArrayList<Product> productArrayList = new ArrayList<>();

                                    if (productsInOrder != null) {
                                        for (Map<String, Object> myMap : productsInOrder) {
                                            Product product = new Product();
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
                                            document.getString("id_user"),
                                            document.getLong("status"));
                                    ordersList.add(order);
                                }
                            }
                            updateAdapter();
                        } else {
                            Log.w(TAG, "Error fetching orders", task.getException());
                        }
                    }
                });
    }

    private void updateAdapter() {
        if (adapter == null) {
            adapter = new UserOrderAdapter(ordersList, getContext());
            binding.usersOrdersRc.setAdapter(adapter);
        } else {
            adapter.getItems().clear();
            adapter.getItems().addAll(ordersList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
