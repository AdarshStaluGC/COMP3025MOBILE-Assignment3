package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3.adapters.MovieAdapter;
import com.example.assignment3.models.MovieModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<MovieModel> favoriteList = new ArrayList<>();

    // Firebase instances
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites each time the screen becomes visible
        fetchFavorites();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Set up the RecyclerView for displaying favorite movies
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter setup: handles the click event to open details
        adapter = new MovieAdapter(this, favoriteList, movie -> {
            Intent intent = new Intent(FavoritesActivity.this, FavoriteDetailsActivity.class);
            intent.putExtra("imdbId", movie.getImdbID()); // Pass IMDb ID to the details screen
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter); // Attach adapter to RecyclerView

        fetchFavorites(); // Load favorite movies

        // Set up bottom navigation and its event handler
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_favorites); // Highlight current tab

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_search) {
                // Navigate to the search/main screen
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                finish(); // Remove current activity from the back stack
                return true;
            }
            return true; // Already on favorites tab
        });
    }

    // 🔹 Fetch favorite movies for the current user from Firestore
    private void fetchFavorites() {
        db.collection("users")
                .document(auth.getCurrentUser().getUid()) // Get current user UID
                .collection("favorites") // Go to their favorites collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteList.clear(); // Clear current list to refresh
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        // Create MovieModel from Firestore fields
                        MovieModel movie = new MovieModel(
                                doc.getString("Title"),
                                doc.getString("Year"),
                                doc.getId(), // IMDb ID is the doc ID
                                "", // Type not used in this case
                                doc.getString("Poster")
                        );
                        favoriteList.add(movie); // Add to list
                    }
                    adapter.notifyDataSetChanged(); // Refresh the UI
                })
                .addOnFailureListener(e ->
                        // Show error message if fetching fails
                        Toast.makeText(this, "Failed to fetch", Toast.LENGTH_SHORT).show());
    }
}
