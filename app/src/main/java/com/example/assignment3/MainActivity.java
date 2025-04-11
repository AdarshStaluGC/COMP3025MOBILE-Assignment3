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

    private ActivityMainBinding binding;
    private List<MovieModel> movieList;
    private MovieAdapter movieAdapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Auth check
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup RecyclerView
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(movieAdapter);

        // Search button click
        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchView.getQuery().toString();
            if (!query.isEmpty()) {
                fetchMovies(query);
                binding.searchView.clearFocus();
            }
        });

        // SearchView submit
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
                return false;
            }
        });

        // BottomNavigationView item selection
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_favorites) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                return true;
            }
            return true; // current is Search
        });
    }

    private void fetchMovies(String query) {
        ApiClient.getMovies(query, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "Failed to fetch movies", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (jsonObject.has("Search")) {
                            JSONArray searchArray = jsonObject.getJSONArray("Search");
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
                            runOnUiThread(() -> {
                                movieList.clear();
                                movieList.addAll(movies);
                                movieAdapter.notifyDataSetChanged();
                            });
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
