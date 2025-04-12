package com.example.assignment3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assignment3.utils.ApiClient;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    // UI Components
    private ImageView moviePoster;
    private TextView movieTitle;
    private Button addToFavoritesBtn;

    // Firebase services
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // 📦 Initialize views
        moviePoster = findViewById(R.id.detail_movie_poster);
        movieTitle = findViewById(R.id.detail_movie_title);
        addToFavoritesBtn = findViewById(R.id.add_to_favorites);

        // 🔥 Firebase setup
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 🎬 Get IMDb ID from intent
        String imdbId = getIntent().getStringExtra("imdbId");

        // 🔍 Fetch full movie details from API
        ApiClient.getMovieDetails(imdbId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MovieDetailsActivity.this, "Failed to fetch movie details", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject movieJson = new JSONObject(jsonData);
                        runOnUiThread(() -> updateUI(movieJson)); // 🖼 Update UI with JSON data
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // ⬅️ Back button functionality
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    /**
     * Updates the screen with full movie data
     */
    private void updateUI(JSONObject movieJson) {
        try {
            // Set the main title
            movieTitle.setText(movieJson.getString("Title"));

            // Fill in detailed text views
            ((TextView) findViewById(R.id.detail_movie_plot)).setText(movieJson.getString("Plot"));
            ((TextView) findViewById(R.id.detail_movie_director)).setText(movieJson.getString("Director"));
            ((TextView) findViewById(R.id.detail_movie_actors)).setText(movieJson.getString("Actors"));
            ((TextView) findViewById(R.id.detail_movie_genre)).setText(movieJson.getString("Genre"));
            ((TextView) findViewById(R.id.detail_movie_awards)).setText(movieJson.getString("Awards"));

            // 🌟 Convert IMDb rating (out of 10) to a 5-star scale
            String rating = movieJson.getString("imdbRating");
            if (!rating.equals("N/A")) {
                float ratingValue = Float.parseFloat(rating) / 2; // Convert to 5-star
                ((RatingBar) findViewById(R.id.detail_movie_rating)).setRating(ratingValue);
            }

            // Set year, rating, and runtime chips
            ((Chip) findViewById(R.id.detail_movie_year)).setText(movieJson.getString("Year"));
            ((Chip) findViewById(R.id.detail_movie_rated)).setText(movieJson.getString("Rated"));
            ((Chip) findViewById(R.id.detail_movie_runtime)).setText(movieJson.getString("Runtime"));

            // 🖼 Load poster image using Glide (with a placeholder)
            Glide.with(this)
                    .load(movieJson.getString("Poster"))
                    .placeholder(R.drawable.placeholder)
                    .into(moviePoster);

            // ⭐ "Add to Favorites" button logic
            addToFavoritesBtn.setOnClickListener(v -> {
                Map<String, Object> movie = new HashMap<>();
                try {
                    // Only save the key details
                    movie.put("Title", movieJson.getString("Title"));
                    movie.put("Year", movieJson.getString("Year"));
                    movie.put("Poster", movieJson.getString("Poster"));
                    movie.put("Plot", movieJson.getString("Plot"));

                    // Save into Firestore under user's favorites
                    db.collection("users")
                            .document(auth.getCurrentUser().getUid())
                            .collection("favorites")
                            .document(movieJson.getString("imdbID")) // Use IMDb ID as doc ID
                            .set(movie)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to save favorite", Toast.LENGTH_SHORT).show());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace(); // Fallback for any JSON issues
        }
    }
}
