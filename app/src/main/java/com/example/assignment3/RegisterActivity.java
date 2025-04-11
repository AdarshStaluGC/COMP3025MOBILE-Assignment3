package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment2.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.registerBtn.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }).addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        binding.toLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
