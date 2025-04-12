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

    private ImageView posterImage;
    private TextView titleText;
    private EditText plotEdit;
    private Button saveBtn, deleteBtn;

    private String imdbId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_details);

        // UI bindings
        posterImage = findViewById(R.id.poster);
        titleText = findViewById(R.id.title);
        plotEdit = findViewById(R.id.plot);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());


        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        imdbId = getIntent().getStringExtra("imdbId");
        docRef = db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("favorites")
                .document(imdbId);

        //  Fetch from Firestore
        docRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String title = doc.getString("Title");
                String poster = doc.getString("Poster");
                String plot = doc.getString("Plot");

                titleText.setText(title != null ? title : "Untitled");
                plotEdit.setText(plot != null ? plot : "No description");
                Glide.with(this)
                        .load(poster)
                        .placeholder(R.drawable.placeholder)
                        .into(posterImage);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load movie", Toast.LENGTH_SHORT).show()
        );

        //  Save edited plot
        saveBtn.setOnClickListener(v -> {
            String updatedPlot = plotEdit.getText().toString().trim();
            Map<String, Object> updates = new HashMap<>();
            updates.put("Plot", updatedPlot);

            docRef.update(updates)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        });


        deleteBtn.setOnClickListener(v -> {
            docRef.delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
        });



    }
}
