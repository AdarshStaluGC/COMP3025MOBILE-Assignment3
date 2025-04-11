package com.example.assignment2;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment2.adapters.MovieAdapter;
import com.example.assignment2.databinding.ActivityMainBinding;
import com.example.assignment2.models.MovieModel;
import com.example.assignment2.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);

        binding.recyclerView.setAdapter(movieAdapter);


        binding.searchButton.setOnClickListener(v -> {
            String query = binding.searchView.getQuery().toString();
            if (!query.isEmpty()) {
                fetchMovies(query);
                binding.searchView.clearFocus();
            }
        });


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
                                        movieJson.getString("Poster")));
                            }
                            runOnUiThread(() -> {
                                movieList.clear();
                                movieList.addAll(movies);
                                movieAdapter.notifyDataSetChanged();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                    "No movies found", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
