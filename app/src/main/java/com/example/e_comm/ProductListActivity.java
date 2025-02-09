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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set up Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list);
        binding.setLifecycleOwner(this);

        FirebaseApp.initializeApp(this);

        // Initialize Firestore and product list
        db = FirebaseFirestore.getInstance();
        productsList = new ArrayList<>();

        // Get Intent Data
        isSeller = getIntent().getBooleanExtra("isSeller", false);
        categoryId = getIntent().getStringExtra("catName");

        // Setup RecyclerView
        binding.productsListRc.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(productsList, this, this::openProductDetails);
        binding.productsListRc.setAdapter(adapter);

        // Show/Hide Add Product Button
        if (isSeller) {
            binding.addProductButton.setVisibility(View.VISIBLE);
            binding.addProductButton.setOnClickListener(v ->
                    startActivity(new Intent(this, AddProductActivity.class))
            );
        } else {
            binding.addProductButton.setVisibility(View.GONE);
        }

        // Fetch Data
        fetchData();
    }

    private void openProductDetails(View view, Product product) {
            Intent productPage = new Intent(this, ProductActivity.class);
            productPage.putExtra("nomItem", product.getNom());
            productPage.putExtra("descItem", product.getDescription());
            productPage.putExtra("idItem", product.getId_product());
            productPage.putStringArrayListExtra("imgItem", new ArrayList<>(product.getImg()));  // Safe conversion
            productPage.putExtra("priceItem", product.getPrice());
            productPage.putExtra("idSeller", product.getId_seller());
            productPage.putExtra("idCat", product.getId_cat());
            startActivity(productPage);
        }


    private void fetchData() {
        db.collection("products").whereEqualTo("id_cat", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productsList.clear();  // Clear old data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> myMap = document.getData();

                            // Ensure imgs is a List<String>
                            List<String> imgs = new ArrayList<>();
                            Object imgData = myMap.get("img");
                            if (imgData instanceof List) {
                                for (Object item : (List<?>) imgData) {
                                    if (item instanceof String) {
                                        imgs.add((String) item);
                                    }
                                }
                            }

                            Product product = new Product(
                                    document.getId(),
                                    (String) myMap.get("id_seller"),
                                    (String) myMap.get("id_cat"),
                                    (String) myMap.get("nom"),
                                    (String) myMap.get("description"),
                                    String.valueOf(imgs),  // Safe list handling
                                    myMap.get("price") instanceof Number ? ((Number) myMap.get("price")).doubleValue() : 0.0
                            );

                            productsList.add(product);
                        }

                        adapter.updateList(new ArrayList<>(productsList)); // Ensures a fresh list reference
                    } else {
                        Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error getting documents", task.getException());
                    }
                });
    }


}

