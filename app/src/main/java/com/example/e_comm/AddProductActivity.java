package com.example.e_comm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.e_comm.databinding.ActivityAddProductBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Uri filePath;
    private Spinner spinner;
    private String base64Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product);
        binding.setLifecycleOwner(this);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set listeners
        binding.pictureAdded.setOnClickListener(v -> pickFromGallery());
        binding.addProductButton.setOnClickListener(v -> onAddProductClicked());

        loadCategories();
    }

    private void loadCategories() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Electronics");
        categories.add("Clothing");
        categories.add("Home & Kitchen");
        categories.add("Books");

        spinner = binding.spinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                base64Image = encodeImage(bitmap);
                Glide.with(this).load(filePath).into(binding.pictureAdded);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void onAddProductClicked() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String productName = binding.fieldAddNom.getText().toString().trim();
        String productDescription = binding.fieldAddDesc.getText().toString().trim();
        String productPrice = binding.fieldAddPrice.getText().toString().trim();
        String productCategory = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : "";

        if (productName.isEmpty() || productDescription.isEmpty() || productPrice.isEmpty() || base64Image.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Product...");
        progressDialog.show();

        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("description", productDescription);
        product.put("price", productPrice);
        product.put("category", productCategory);
        product.put("image", base64Image);

        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Products").add(product)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
