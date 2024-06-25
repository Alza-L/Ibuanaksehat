package com.example.bidan.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bidan.MainActivity;
import com.example.bidan.databinding.ActivitySigninBinding;
import com.example.bidan.utilities.Constants;
import com.example.bidan.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigninActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setListener();
    }

    private void setListener() {
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidInput()) {
                signIn();
            }
        });

        binding.textCreateNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidInput() {
        if (TextUtils.isEmpty(binding.inputPhone.getText().toString().trim()) || TextUtils.isEmpty(binding.inputPassword.getText().toString().trim())) {
            showToast("Please enter phone and password");
            return false;
        }
        return true;
    }

    private void signIn() {
        loading(true);
        String phone = binding.inputPhone.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_BIDAN)
                .whereEqualTo(Constants.BIDAN_PHONE, phone)
                .whereEqualTo(Constants.BIDAN_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String bidanId = documentSnapshot.getId();

                        preferenceManager.putBoolean(Constants.IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.BIDAN_ID, bidanId);
                        preferenceManager.putString(Constants.BIDAN_IMAGE, documentSnapshot.getString(Constants.BIDAN_IMAGE));
                        preferenceManager.putString(Constants.BIDAN_NAME, documentSnapshot.getString(Constants.BIDAN_NAME));
                        preferenceManager.putString(Constants.BIDAN_PHONE, documentSnapshot.getString(Constants.BIDAN_PHONE));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("Login gagal! Periksa nomor telepon atau password Anda");
                    }
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast("Tidak dapat masuk. Periksa koneksi internet Anda!");
                });
    }
}