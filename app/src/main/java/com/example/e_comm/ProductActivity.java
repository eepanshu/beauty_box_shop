package com.example.e_comm;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import com.example.e_comm.Helpers.Singleton;
import com.example.e_comm.adapters.ImageAdapter;
import com.example.e_comm.databinding.ActivityProductBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";
    private ActivityProductBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String idItem, idSeller;
    private boolean isClicked = false;
    private ArrayList<String> imgItem;
    private double priceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeData();
        configureUI();
        fetchSellerInfo();
        setupButtonClickListener();
    }

    private void initializeData() {
        user = Singleton.getUser();
        db = Singleton.getDb();

        idItem = getIntent().getStringExtra("idItem");
        idSeller = getIntent().getStringExtra("idSeller");
        priceItem = getIntent().getDoubleExtra("priceItem", 0.0);
        imgItem = (ArrayList<String>) getIntent().getSerializableExtra("imgItem");
    }

    private void configureUI() {
        binding.titleItem.setText(getIntent().getStringExtra("nomItem"));
        binding.descriptionItem.setText(getIntent().getStringExtra("descItem"));
        binding.priceItem.setText(String.format("%.2f Rupees", priceItem));

//        if (imgItem != null && !imgItem.isEmpty()) {
//            ImageAdapter adapter = new ImageAdapter(this, imgItem);
//            binding.imageViewPager.setAdapter(adapter);
//        } else {
            ArrayList<String> defaultImage = new ArrayList<>();
            defaultImage.add("android.resource://" + getPackageName() + "/" + R.drawable.placeholder);
            ImageAdapter adapter = new ImageAdapter(this, defaultImage);
            binding.imageViewPager.setAdapter(adapter);


    }

    private void fetchSellerInfo() {
        db.collection("Users").document(idSeller).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    binding.sellerName.setText(document.getString("Nom"));
                    Glide.with(ProductActivity.this).load(document.getString("image")).into(binding.profileImage);
                }
            } else {
                Log.e(TAG, "Error fetching seller data", task.getException());
            }
        });
    }

    private void setupButtonClickListener() {
        binding.buyNow.setOnClickListener(v -> {
            isClicked = !isClicked;
            String message = isClicked ? "Added to cart" : "Removed from cart";
            int color = isClicked ? R.color.md_red_400 : R.color.md_black_1000;

            Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
            binding.buyNow.setBackgroundColor(getResources().getColor(color));
        });
    }
}
