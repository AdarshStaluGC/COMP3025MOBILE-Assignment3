package com.example.assignment3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class FavoriteDetailsActivity extends AppCompatActivity {

    // UI components
    private ImageView posterImage;
    private TextView titleText;
    private EditText plotEdit;
    private Button saveBtn, deleteBtn;

    // Variables for Firestore
    private String imdbId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_details);

        // Initialize and bind UI elements
        posterImage = findViewById(R.id.poster);
        titleText = findViewById(R.id.title);
        plotEdit = findViewById(R.id.plot);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        // Back button to return to previous screen
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        // Initialize Firebase Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Retrieve the IMDb ID passed from the previous activity
        imdbId = getIntent().getStringExtra("imdbId");

        // Build a reference to the specific favorite movie document for the logged-in user
        docRef = db.collection("users")
                .document(auth.getCurrentUser().getUid())  // Gets current user's UID
                .collection("favorites")
                .document(imdbId);  // Document ID is the IMDb ID

        // 🔹 FETCH MOVIE DATA FROM FIRESTORE
        docRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                // Extract values from the document
                String title = doc.getString("Title");
                String poster = doc.getString("Poster");
                String plot = doc.getString("Plot");

                // Update UI with retrieved data
                titleText.setText(title != null ? title : "Untitled");
                plotEdit.setText(plot != null ? plot : "No description");

                // Load poster image using Glide (with a placeholder)
                Glide.with(this)
                        .load(poster)
                        .placeholder(R.drawable.placeholder)
                        .into(posterImage);
            }
        }).addOnFailureListener(e ->
                // Handle failure to load data
                Toast.makeText(this, "Failed to load movie", Toast.LENGTH_SHORT).show()
        );

        // 🔹 SAVE (UPDATE) MOVIE PLOT
        saveBtn.setOnClickListener(v -> {
            String updatedPlot = plotEdit.getText().toString().trim();  // Get user input

            // Create map of updates to apply
            Map<String, Object> updates = new HashMap<>();
            updates.put("Plot", updatedPlot);

            // Update the "Plot" field in Firestore
            docRef.update(updates)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        });

        // 🔹 DELETE MOVIE FROM FAVORITES
        deleteBtn.setOnClickListener(v -> {
            docRef.delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();  // Go back after deletion
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
        });
    }
}
