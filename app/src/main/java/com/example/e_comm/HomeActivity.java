package com.example.e_comm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_comm.adapters.CategoriesAdapter;
import com.example.e_comm.adapters.ProductAdapter;
import com.example.e_comm.clicklistners.RecyclerViewClickListener;
import com.example.e_comm.clicklistners.RecyclerViewClickListenerProduct;
import com.example.e_comm.databinding.ActivityHomeBinding;
import com.example.e_comm.models.Categories;
import com.example.e_comm.models.Product;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private CategoriesAdapter categoriesAdapter;
    private ProductAdapter productsAdapter;
    private final ArrayList<Categories> categoryList = new ArrayList<>();
    private final ArrayList<Product> productList = new ArrayList<>();

    private RecyclerViewClickListener recyclerViewClickListener;
    private RecyclerViewClickListenerProduct recyclerViewClickListenerProduct;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbar);

        setupBottomNavigation();
        initializeRecyclerViews();
        fetchCategories();
        fetchProducts();  // Fetch products with the Base64 encoded image
    }

    private void setupBottomNavigation() {
        binding.navigationHome.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class<?> activityClass = null;
                if (item.getItemId() == R.id.home_button) {
                    activityClass = UsersOrders.class;
                } else if (item.getItemId() == R.id.navigation_cart) {
                    activityClass = CartActivity.class;
                } else if (item.getItemId() == R.id.profil_button) {
                    activityClass = ProfileActivity.class;
                }

                if (activityClass != null) {
                    startActivity(new Intent(HomeActivity.this, activityClass));
                    return true;
                }
                return false;
            }
        });
    }

    private void initializeRecyclerViews() {
        binding.categoriesRc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.productsRc.setLayoutManager(new GridLayoutManager(this, 2));
        binding.productsRc.setNestedScrollingEnabled(false);

        // Initialize adapters
        categoriesAdapter = new CategoriesAdapter(categoryList, this, recyclerViewClickListener);
        productsAdapter = new ProductAdapter(productList, this, recyclerViewClickListenerProduct);

        binding.categoriesRc.setAdapter(categoriesAdapter);
        binding.productsRc.setAdapter(productsAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCategories() {
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                categoryList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    Categories category = new Categories(
                            document.getId(),
                            (String) data.get("name"),
                            (String) data.get("icon"),
                            (String) data.get("color")
                    );
                    categoryList.add(category);
                }
                categoriesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to fetch categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchProducts() {
        db.collection("Products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    ArrayList<String> images = (ArrayList<String>) data.get("img");

                    // Retrieve the Base64-encoded image string
                    String base64Image = (String) data.get("imageBase64");

                    // Safely handle price conversion (in case it's a String)
                    Object priceObject = data.get("price");
                    Double price = 0.0;
                    if (priceObject instanceof Double) {
                        price = (Double) priceObject;
                    } else if (priceObject instanceof String) {
                        try {
                            price = Double.parseDouble((String) priceObject);
                        } catch (NumberFormatException e) {
                            e.printStackTrace(); // Handle the exception if necessary
                        }
                    }

                    Product product = new Product(
                            document.getId(),
                            (String) data.get("id_seller"),
                            (String) data.get("id_cat"),
                            (String) data.get("nom"),
                            (String) data.get("description"),
                            images,
                            price,
                            base64Image  // Add the Base64 image data to the product
                    );
                    productList.add(product);
                }
                productsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
