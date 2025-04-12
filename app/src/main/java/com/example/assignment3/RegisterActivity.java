package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment3.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    // ViewBinding for accessing layout views
    private ActivityRegisterBinding binding;

    // Firebase authentication
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding to connect UI layout
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // 🎯 Register button click
        binding.registerBtn.setOnClickListener(v -> {
            // Get email and password input
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            // 🔐 Create new Firebase user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // ✅ Registration successful — go to main app screen
                        startActivity(new Intent(this, MainActivity.class));
                        finish(); // Close RegisterActivity so it's not in the back stack
                    })
                    .addOnFailureListener(e -> {
                        // ❌ Show error message if registration fails
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // 🔄 Navigate to Login screen if user already has an account
        binding.toLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close RegisterActivity to prevent back stack looping
        });
    }
}
