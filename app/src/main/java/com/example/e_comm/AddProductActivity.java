package com.example.e_comm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityAddProductBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Uri filePath;
    private Spinner categorySpinner;
    private ProgressDialog progressDialog;
    private String encodedImage; // Stores the Base64-encoded image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product);
        binding.setLifecycleOwner(this);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Setup UI interactions
        binding.pictureAdded.setOnClickListener(v -> pickImageFromGallery());
        binding.addProductButton.setOnClickListener(v -> addProduct());

        setupCategorySpinner();
    }

    private void setupCategorySpinner() {
        String[] categories = {"Electronics", "Clothing", "Home & Kitchen", "Books"};
        categorySpinner = binding.spinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Glide.with(this).load(filePath).into(binding.pictureAdded);
            encodeImageToBase64(filePath); // Convert the image to Base64
        }
    }

    private void encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            showToast("Error encoding image: " + e.getMessage());
        }
    }

    private void addProduct() {
        if (auth.getCurrentUser() == null) {
            showToast("User not authenticated. Please log in again.");
            return;
        }

        String productName = binding.fieldAddNom.getText().toString().trim();
        String productDescription = binding.fieldAddDesc.getText().toString().trim();
        String productPrice = binding.fieldAddPrice.getText().toString().trim();
        String productCategory = categorySpinner.getSelectedItem().toString();

        if (validateInputs(productName, productDescription, productPrice, productCategory)) {
            saveProductToFirestore(productName, productDescription, productPrice, productCategory);
        }
    }

    private boolean validateInputs(String name, String description, String price, String category) {
        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || encodedImage == null) {
            showToast("Please fill all fields and select an image.");
            return false;
        }

        try {
            Double.parseDouble(price); // Validate numeric price
        } catch (NumberFormatException e) {
            showToast("Please enter a valid price.");
            return false;
        }

        return true;
    }

    private void saveProductToFirestore(String name, String description, String price, String category) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Product...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String userId = auth.getCurrentUser().getUid();
        String productId = db.collection("Products").document().getId();

        Map<String, Object> product = new HashMap<>();
        product.put("productId", productId);
        product.put("name", name);
        product.put("description", description);
        product.put("price", price);
        product.put("category", category);
        product.put("sellerId", userId);
        product.put("imageBase64", encodedImage); // Storing the Base64 image string

        // Store product in Firestore
        db.collection("Products").document(productId)
                .set(product)
                .addOnSuccessListener(aVoid -> storeProductInUserCollection(userId, productId, product))
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    showToast("Error adding product: " + e.getMessage());
                });
    }

    private void storeProductInUserCollection(String userId, String productId, Map<String, Object> product) {
        db.collection("Users").document(userId)
                .collection("MyProducts").document(productId)
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    dismissProgressDialog();
                    showToast("Product added successfully!");
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    showToast("Error saving to user collection: " + e.getMessage());
                });
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
