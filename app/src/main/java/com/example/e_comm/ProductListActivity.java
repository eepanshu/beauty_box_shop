package com.example.e_comm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.e_comm.adapters.ProductAdapter;
import com.example.e_comm.databinding.ActivityProductListBinding;
import com.example.e_comm.models.Product;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Product> productsList;
    private ProductAdapter adapter;
    private boolean isSeller;
    private String categoryId;
    private ActivityProductListBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list);
        binding.setLifecycleOwner(this);

        auth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);


        db = FirebaseFirestore.getInstance();
        productsList = new ArrayList<>();


        isSeller = getIntent().getBooleanExtra("isSeller", false);
        categoryId = getIntent().getStringExtra("catName");


        binding.productsListRc.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(productsList, this, this::openProductDetails);
        binding.productsListRc.setAdapter(adapter);


        if (isSeller) {
            binding.addProductButton.setVisibility(View.VISIBLE);
            binding.addProductButton.setOnClickListener(v ->
                    startActivity(new Intent(this, AddProductActivity.class))
            );
        } else {
            binding.addProductButton.setVisibility(View.GONE);
        }


        fetchData();
    }

    private void openProductDetails(View view, Product product) {
        if (product == null) return; // Prevent NullPointerException

        Intent productPage = new Intent(this, ProductActivity.class);
        productPage.putExtra("nomItem", product.getNom());
        productPage.putExtra("descItem", product.getDescription());
        productPage.putExtra("idItem", product.getId_product());
        productPage.putExtra("priceItem", product.getPrice());
        productPage.putExtra("idSeller", product.getId_seller());
        productPage.putExtra("idCat", product.getId_cat());

        // Ensure product.getImg() is not null before converting
        ArrayList<String> imageList = (product.getImg() != null) ? new ArrayList<>(product.getImg()) : new ArrayList<>();
        productPage.putStringArrayListExtra("imgItem", imageList);

        startActivity(productPage);
    }



    private void fetchData() {
        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("MyProducts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productsList.clear();  // Clear old data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> myMap = document.getData();

                            // Retrieve image URI
                            String imgUri = myMap.get("imageUri") != null ? myMap.get("imageUri").toString() : "";

                            // **Fix: Ensure price is correctly parsed**
                            double price = 0.0;
                            if (myMap.get("price") instanceof Number) {
                                price = ((Number) myMap.get("price")).doubleValue();
                            } else if (myMap.get("price") instanceof String) {
                                try {
                                    price = Double.parseDouble((String) myMap.get("price"));
                                } catch (NumberFormatException e) {
                                    Log.e("PriceError", "Invalid price format: " + myMap.get("price"));
                                }
                            }


                            Product product = new Product(
                                    document.getId(),
                                    (String) myMap.get("sellerId"),
                                    (String) myMap.get("category"),
                                    (String) myMap.get("name"),
                                    (String) myMap.get("description"),
                                    imgUri,
                                    price
                            );

                            productsList.add(product);
                        }

                        adapter.updateList(new ArrayList<>(productsList)); // Ensures fresh list reference
                    } else {
                        Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error getting documents", task.getException());
                    }
                });
    }




}

