package com.example.user.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.MainActivity;
import com.example.user.databinding.ActivityLoginBinding;
import com.example.user.utilities.Constants;
import com.example.user.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
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
        binding.buttonLogin.setOnClickListener(v -> {
            emailValidator();
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonLogin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonLogin.setVisibility(View.VISIBLE);
        }
    }

    private void emailValidator() {
        String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        String email = binding.inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("Email tidak boleh kosong");
            binding.inputEmail.requestFocus();
        } else {
            if (!pattern.matcher(email).matches()) {
                showToast("Email tidak valid");
                binding.inputEmail.requestFocus();
            } else {
                login(email);
            }
        }
    }

    private void login(String email) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.USER_EMAIL, email);
        database.collection(Constants.COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    String userId = documentReference.getId();
                    preferenceManager.putBoolean(Constants.IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.USER_ID, userId);
                    preferenceManager.putString(Constants.USER_EMAIL, binding.inputEmail.getText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast("Login gagal");
                });
    }
}