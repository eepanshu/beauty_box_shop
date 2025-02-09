package com.example.e_comm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;


import com.example.e_comm.HomeActivity;
import com.example.e_comm.RegisterActivity;
import com.example.e_comm.SellerHomeActivity;
import com.example.e_comm.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.emailSignInButton.setOnClickListener(v -> signIn());
        binding.emailCreateAccountButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRole();
        }
    }

    private void signIn() {
        String email = binding.fieldEmail.getText().toString().trim();
        String password = binding.fieldPassword.getText().toString().trim();

        if (!validateForm(email, password)) return;

//        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
//                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                        checkUserRole();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.setError("Required");
            valid = false;
        } else {
            binding.fieldEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.setError("Required");
            valid = false;
        } else {
            binding.fieldPassword.setError(null);
        }

        return valid;
    }

    private void checkUserRole() {
//        showProgressDialog();
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
//                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Intent intent;
                            if (Boolean.TRUE.equals(document.getBoolean("seller"))) {
                                intent = new Intent(this, SellerHomeActivity.class);
                            } else {
                                intent = new Intent(this, HomeActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }
//
//    private void showProgressDialog() {
//        binding.progressBar.setVisibility(View.VISIBLE);
//    }
//
//    private void hideProgressDialog() {
//        binding.progressBar.setVisibility(View.GONE);
//    }
}
