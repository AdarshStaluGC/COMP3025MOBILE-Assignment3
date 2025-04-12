package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment3.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // View binding to access UI components
    private ActivityLoginBinding binding;

    // Firebase Authentication instance
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // 🔹 LOGIN BUTTON HANDLER
        binding.loginBtn.setOnClickListener(v -> {
            // Get and trim user inputs
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            // Attempt to sign in using Firebase Auth
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Login successful – navigate to MainActivity
                        startActivity(new Intent(this, MainActivity.class));
                        finish(); // Close LoginActivity
                    })
                    .addOnFailureListener(e ->
                            // Show error if login fails
                            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // 🔹 REGISTER LINK HANDLER
        binding.toRegister.setOnClickListener(v ->
                // Open RegisterActivity when user clicks "Don't have an account?"
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
