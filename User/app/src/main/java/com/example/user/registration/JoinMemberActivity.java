package com.example.user.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.MainActivity;
import com.example.user.databinding.ActivityJoinMemberBinding;
import com.example.user.utilities.Constants;
import com.example.user.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class JoinMemberActivity extends AppCompatActivity {
    private ActivityJoinMemberBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();
    }

    private void setListener() {
        binding.iconBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.buttonGabung.setOnClickListener(v -> {
            if (inputValidation()) {
                joinMember();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonGabung.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonGabung.setVisibility(View.VISIBLE);
        }
    }

    private boolean inputValidation() {
        if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Nama tidak boleh kosong");
            binding.inputName.requestFocus();
            return false;
        } else if (binding.inputPhone.getText().toString().trim().isEmpty()) {
            showToast("Nomor Whatsapp tidak boleh kosong");
            binding.inputPhone.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void joinMember() {
        loading(true);
        String userId = preferenceManager.getString(Constants.USER_ID);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.USER_NAME, binding.inputName.getText().toString().trim());
        user.put(Constants.USER_PHONE, binding.inputPhone.getText().toString().trim());
        database.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.IS_MEMBER, true);
                    preferenceManager.putString(Constants.USER_FIRST_NAME, getFirstName());
                    preferenceManager.putString(Constants.USER_NAME, binding.inputName.getText().toString().trim());
                    preferenceManager.putString(Constants.USER_PHONE, binding.inputPhone.getText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast("Gagal mendaftar");
                });
    }

    private String getFirstName() {
        String fullName = binding.inputName.getText().toString().trim();
        String[] nameParts = fullName.split(" ");
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        preferenceManager.putString(Constants.USER_FIRST_NAME, firstName);
        return firstName;
    }
}