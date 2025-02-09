package com.example.e_comm;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.e_comm.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.emailCreateAccountButton.setOnClickListener(v -> createAccount());
        binding.signedInButton.setOnClickListener(v -> navigateToLogin());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in
            Log.d(TAG, "User already signed in: " + currentUser.getEmail());
        }
    }

    private void createAccount() {
        String email = binding.fieldEmail.getText().toString().trim();
        String password = binding.fieldPassword.getText().toString().trim();
        String repass = binding.fieldrePassword.getText().toString().trim();

        if (!validateForm(email, password, repass)) {
            return;
        }

        if (!isConnectedToInternet()) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "User registered successfully: " + user.getEmail());
                            putData(user.getUid(), user.getEmail());
                        }
                    } else {
                        Log.e(TAG, "Registration Failed: ", task.getException());
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm(String email, String password, String repass) {
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.fieldEmail.setError("Enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            binding.fieldPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(repass)) {
            binding.fieldPassword.setError("Passwords do not match");
            binding.fieldrePassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void putData(String userId, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("Nom", binding.fieldNom.getText().toString().trim());
        user.put("Prenom", binding.fieldPrenom.getText().toString().trim());
        user.put("Telephone", "");
        user.put("image", "");
        user.put("seller", binding.checkSeller.isChecked());

        db.collection("Users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data added to Firestore");
                    getIdS();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error writing document", e);
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void getIdS() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("Users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            boolean isSeller = Boolean.TRUE.equals(document.getBoolean("seller"));
                            Intent intent = new Intent(RegisterActivity.this, isSeller ? SellerProfileActivity.class : HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Document does not exist!");
                        }
                    } else {
                        Log.e(TAG, "Error fetching user data", task.getException());
                    }
                });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
