package com.example.e_comm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.e_comm.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
//    private FirebaseStorage storage;
//    private StorageReference storageReference;
    private Uri selectedImageUri;
//
//    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    selectedImageUri = result.getData().getData();
//                    binding.profileImage.setImageURI(selectedImageUri);
//                    uploadProfileImage();
//                }
//            }
//    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference("Profiles");
//
//        loadUserProfile();
//
//        binding.profileImage.setOnClickListener(v -> selectImage());
    }

//    private void loadUserProfile() {
//        String userId = auth.getCurrentUser().getUid();
//        db.collection("Users").document(userId).get().addOnSuccessListener(document -> {
//            if (document.exists()) {
//                binding.username.setText(document.getString("name"));
//                if (document.contains("image") && document.getString("image") != null) {
//                    Glide.with(this).load(document.getString("image")).into(binding.profileImage);
//                }
//            }
//        });
//    }

//    private void selectImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        imagePickerLauncher.launch(intent);
//    }

//    private void uploadProfileImage() {
//        if (selectedImageUri == null) return;
//
//        StorageReference ref = storageReference.child(auth.getCurrentUser().getUid() + ".jpg");
//        ref.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
//                ref.getDownloadUrl().addOnSuccessListener(uri -> {
//                    updateUserProfile(uri.toString());
//                })
//        );
//    }

//    private void updateUserProfile(String imageUrl) {
//        String userId = auth.getCurrentUser().getUid();
//        HashMap<String, Object> userData = new HashMap<>();
//        userData.put("image", imageUrl);
//
//        db.collection("Users").document(userId).update(userData);
//    }
}
