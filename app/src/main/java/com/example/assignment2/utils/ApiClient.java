package com.example.assignment2.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiClient {
    // Create a single instance of OkHttpClient
    private static final OkHttpClient client = new OkHttpClient();

    // API key for accessing OMDB API
    private static final String API_KEY = "39ceac9";

    // Base URL for OMDB API with API key included
    private static final String BASE_URL = "https://www.omdbapi.com/?apikey=" + API_KEY;

    // Method to fetch movies based on search query
    public static void getMovies(String query, Callback callback) {
        String url = BASE_URL + "&s=" + query; // Construct the search URL
        Request request = new Request.Builder().url(url).build(); // Create a request
        client.newCall(request).enqueue(callback); // Execute request asynchronously
    }

    // Method to fetch movie details using IMDb ID
    public static void getMovieDetails(String imdbId, Callback callback) {
        String url = BASE_URL + "&i=" + imdbId; // Construct the details URL
        Request request = new Request.Builder().url(url).build(); // Create a request
        client.newCall(request).enqueue(callback); // Execute request asynchronously
    }
}
