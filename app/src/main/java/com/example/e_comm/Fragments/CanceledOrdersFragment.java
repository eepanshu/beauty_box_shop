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
import com.example.e_comm.adapters.SellerOrdersAdapter;
import com.example.e_comm.clicklistners.RecyclerViewClickListenerOrder;
import com.example.e_comm.databinding.FragmentCanceledOrdersBinding;
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
import java.util.Set;
import java.util.TreeSet;

public class CanceledOrdersFragment extends Fragment {

    private FragmentCanceledOrdersBinding binding;
    private FirebaseFirestore db;
//    private SellerOrdersAdapter adapter;
    private ArrayList<Order> ordersList;
    private FirebaseUser user;
    private static final String TAG = "CanceledOrdersFragment";
    private SellerOrdersAdapter adapter;

    public CanceledOrdersFragment() {
        // Required empty public constructor
    }

    public static CanceledOrdersFragment newInstance() {
        return new CanceledOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize View Binding
        binding = FragmentCanceledOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase Firestore and User
        db = Singleton.getDb();
        user = Singleton.getUser();
        ordersList = new ArrayList<>();



//         Setup RecyclerView
        binding.canceledOrdersRc.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.canceledOrdersRc.setHasFixedSize(true);

//         Fetch canceled orders
        getCanceledOrders();

        return view;
    }

    private void getCanceledOrders() {
        db.collection("orders")
                .whereEqualTo("status", 2) // Status 2 = Canceled Orders
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            ordersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<Map<String, Object>> productsInOrder =
                                        (ArrayList<Map<String, Object>>) document.get("products");
                                ArrayList<Product> productArrayList = new ArrayList<>();
                                Set<String> sellerIds = new TreeSet<>();

                                if (productsInOrder != null) {
                                    for (Map<String, Object> productMap : productsInOrder) {
                                        Product product = new Product();
                                        product.setPrice((Double) productMap.get("price"));
                                        product.setImg_product(productMap.get("img_product").toString());
                                        product.setNom(productMap.get("nom").toString());
                                        product.setId_seller(productMap.get("id_seller").toString());
                                        product.setDescription(productMap.get("description").toString());
                                        product.setId_cat(productMap.get("id_cat").toString());

                                        if (user.getUid().equals(product.getId_seller())) {
                                            productArrayList.add(product);
                                        }
                                        sellerIds.add(product.getId_seller());
                                    }
                                }

                                for (String sellerId : sellerIds) {
                                    if (user.getUid().equals(sellerId)) {
                                        Order order = new Order(
                                                document.getId(),
                                                productArrayList,
                                                (String) document.get("id_user"),
                                                (Long) document.get("status")
                                        );
                                        ordersList.add(order);
                                    }
                                }
                            }
//                            updateRecyclerView();
                        } else {
                            Log.e(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void updateRecyclerView() {
        if (adapter == null) {
            adapter = new SellerOrdersAdapter(ordersList, getContext(), new RecyclerViewClickListenerOrder() {
                @Override
                public void onClick(View view, Order product) {

                }


            });
            binding.canceledOrdersRc.setAdapter(adapter);
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
