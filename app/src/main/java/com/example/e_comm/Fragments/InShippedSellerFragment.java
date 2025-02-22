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
import androidx.recyclerview.widget.RecyclerView;


import com.example.e_comm.Helpers.Singleton;
import com.example.e_comm.R;
import com.example.e_comm.adapters.SellerOrdersAdapter;
import com.example.e_comm.databinding.FragmentInShippedSellerBinding;
import com.example.e_comm.models.Order;
import com.example.e_comm.models.Product;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InShippedSellerFragment extends Fragment {

    private static final String TAG = "InShippedSellerFragment";

    private FragmentInShippedSellerBinding binding;
    private FirebaseFirestore db;
    private SellerOrdersAdapter adapter;
    private ArrayList<Order> ordersList = new ArrayList<>();
    private FirebaseUser user;

    public InShippedSellerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Singleton.getDb();
        user = Singleton.getUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInShippedSellerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        fetchOrders();
    }

    private void setupRecyclerView() {
        binding.usersOrdersRcInshipped.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.usersOrdersRcInshipped.setHasFixedSize(true);
        adapter = new SellerOrdersAdapter(ordersList, requireContext(), null);
        binding.usersOrdersRcInshipped.setAdapter(adapter);
    }

    private void fetchOrders() {
        db.collection("orders").whereEqualTo("status", 1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ordersList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ArrayList<Map> productsInOrder = (ArrayList<Map>) document.get("products");
                            ArrayList<Product> productArrayList = new ArrayList<>();
                            Set<String> sellerIds = new TreeSet<>();

                            if (productsInOrder != null) {
                                for (Map myMap : productsInOrder) {
                                    Product product = new Product();
                                    product.setPrice((Double) myMap.get("price"));
                                    product.setImg_product(myMap.get("img_product").toString());
                                    product.setNom(myMap.get("nom").toString());
                                    product.setId_seller(myMap.get("id_seller").toString());
                                    product.setDescription(myMap.get("description").toString());
                                    product.setId_cat(myMap.get("id_cat").toString());

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

                        Log.v(TAG, "Orders Loaded: " + ordersList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error getting orders", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
