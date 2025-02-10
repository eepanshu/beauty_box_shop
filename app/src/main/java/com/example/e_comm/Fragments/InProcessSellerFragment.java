package com.example.e_comm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_comm.Helpers.Singleton;
import com.example.e_comm.adapters.SellerOrdersAdapter;
import com.example.e_comm.databinding.FragmentInProcessSellerBinding;
import com.example.e_comm.models.Order;
import com.example.e_comm.models.Product;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class InProcessSellerFragment extends Fragment {

    private FragmentInProcessSellerBinding binding;
    private static final String TAG = "InProcessSellerFragment";

    private FirebaseFirestore db;
    private SellerOrdersAdapter adapter;
    private ArrayList<Order> ordersList;
    private FirebaseUser user;

    public InProcessSellerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInProcessSellerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Singleton.getDb();
        user = Singleton.getUser();
        ordersList = new ArrayList<>();

        setupRecyclerView();
        fetchOrders();
    }

    private void setupRecyclerView() {
        binding.usersOrdersRcInprocess.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersOrdersRcInprocess.setHasFixedSize(true);

        adapter = new SellerOrdersAdapter(new ArrayList<>(), getContext(), (view, order) -> {
            // Handle item click here
            Log.d(TAG, "Order Clicked: " + order.getId_order());
        });

        binding.usersOrdersRcInprocess.setAdapter(adapter);
    }


    private void fetchOrders() {
        db.collection("orders")
                .whereEqualTo("status", 0)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ordersList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ArrayList<Map<String, Object>> productsInOrder =
                                    (ArrayList<Map<String, Object>>) document.get("products");

                            ArrayList<Product> productArrayList = new ArrayList<>();
                            TreeSet<String> sellerIds = new TreeSet<>();

                            if (productsInOrder != null) {
                                for (Map<String, Object> map : productsInOrder) {
                                    Product product = new Product();
                                    product.setPrice((Double) map.get("price"));
                                    product.setImg_product(map.get("img_product").toString());
                                    product.setNom(map.get("nom").toString());
                                    product.setId_seller(map.get("id_seller").toString());
                                    product.setDescription(map.get("description").toString());
                                    product.setId_cat(map.get("id_cat").toString());

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
                                            document.getString("id_user"),
                                            document.getLong("status")
                                    );
                                    ordersList.add(order);
                                }
                            }
                        }

                        Log.d(TAG, "Orders fetched: " + ordersList);

                    } else {
                        Log.w(TAG, "Error fetching orders.", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
