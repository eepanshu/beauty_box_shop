package com.example.e_comm;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import com.example.e_comm.databinding.ActivitySellerHomeBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SellerHomeActivity extends AppCompatActivity {

    private ActivitySellerHomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySellerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup Toolbar
        setSupportActionBar(binding.toolbar);

        // Setup Drawer Layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle Navigation View Item Selection
        binding.navView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item);
            return true;
        });

        // Click Listeners
        binding.productsSeller.setOnClickListener(v -> openProductsList());
        binding.ordersSeller.setOnClickListener(v -> openOrdersList());

        // Fetch Products Count
        getProducts();
    }

    private void handleNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profil) {
            startActivity(new Intent(this, ProfileActivity.class));
            finishAffinity();
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void getProducts() {
        db.collection("products").whereEqualTo("id_seller", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            count++;
                        }
                        binding.productcounts.setText(String.valueOf(count));
                    }
                });
    }

    private void openProductsList() {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("isSeller", true);
        intent.putExtra("sellerID", mAuth.getCurrentUser().getUid());
        startActivity(intent);
    }

    private void openOrdersList() {
        startActivity(new Intent(this, SellerOrdersActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
