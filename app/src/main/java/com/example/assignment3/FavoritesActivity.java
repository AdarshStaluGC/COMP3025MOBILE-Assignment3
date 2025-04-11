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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onResume() {
        super.onResume();
        fetchFavorites();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, favoriteList, movie -> {
            Intent intent = new Intent(FavoritesActivity.this, FavoriteDetailsActivity.class);
            intent.putExtra("imdbId", movie.getImdbID());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        fetchFavorites();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_favorites); // Set the current tab

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_search) {
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                finish(); // Prevent stack piling
                return true;
            }
            return true; // Already on favorites
        });

    }


    private void fetchFavorites() {
        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        MovieModel movie = new MovieModel(
                                doc.getString("Title"),
                                doc.getString("Year"),
                                doc.getId(), // imdbId
                                "", // type unused
                                doc.getString("Poster")
                        );
                        favoriteList.add(movie);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch", Toast.LENGTH_SHORT).show());
    }
}
