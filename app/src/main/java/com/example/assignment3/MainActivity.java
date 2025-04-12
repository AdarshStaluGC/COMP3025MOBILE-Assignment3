package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.assignment3.adapters.MovieAdapter;
import com.example.assignment3.databinding.ActivityMainBinding;
import com.example.assignment3.models.MovieModel;
import com.example.assignment3.utils.ApiClient;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // View binding object to access XML layout components
    private ActivityMainBinding binding;

    // List and adapter for displaying movies in RecyclerView
    private List<MovieModel> movieList;
    private MovieAdapter movieAdapter;

    // Firebase Authentication instance
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔐 Check if user is logged in
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            // Redirect to login if not authenticated
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 📽 Set up RecyclerView for displaying movies
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2-column grid
        binding.recyclerView.setAdapter(movieAdapter);

        // 🔍 Search button click listener
        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchView.getQuery().toString();
            if (!query.isEmpty()) {
                fetchMovies(query);
                binding.searchView.clearFocus(); // Hide keyboard
            }
        });

        // 🔎 Handle keyboard "submit" in SearchView
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    fetchMovies(query);
                    binding.searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: implement live search
                return false;
            }
        });

        // ⬇️ Bottom navigation setup
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_favorites) {
                // Navigate to favorites screen
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                return true;
            }
            return true; // Already on search tab
        });
    }

    // 📡 API call to fetch movies from OMDb API (via ApiClient)
    private void fetchMovies(String query) {
        ApiClient.getMovies(query, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Run on UI thread to show toast
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (jsonObject.has("Search")) {
                            JSONArray searchArray = jsonObject.getJSONArray("Search");

                            // Parse movie results
                            List<MovieModel> movies = new ArrayList<>();
                            for (int i = 0; i < searchArray.length(); i++) {
                                JSONObject movieJson = searchArray.getJSONObject(i);
                                movies.add(new MovieModel(
                                        movieJson.getString("Title"),
                                        movieJson.getString("Year"),
                                        movieJson.getString("imdbID"),
                                        movieJson.getString("Type"),
                                        movieJson.getString("Poster")
                                ));
                            }

                            // Update UI on the main thread
                            runOnUiThread(() -> {
                                movieList.clear();
                                movieList.addAll(movies);
                                movieAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                            });
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace(); // Log error for debugging
                    }
                }
            }
        });
    }
}
