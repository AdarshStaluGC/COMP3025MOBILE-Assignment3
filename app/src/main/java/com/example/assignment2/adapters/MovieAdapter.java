package com.example.assignment2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment2.MovieDetailsActivity;
import com.example.assignment2.R;
import com.example.assignment2.models.MovieModel;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<MovieModel> movieList;

    // Constructor to initialize context and movie list
    public MovieAdapter(Context context, List<MovieModel> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout for each movie item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Get the current movie
        MovieModel movie = movieList.get(position);

        // Set movie title and release year
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());

        // Load movie poster using Glide
        Glide.with(holder.poster.getContext())
                .load(movie.getPoster())
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .into(holder.poster);

        // Handle item click to open MovieDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("imdbId", movie.getImdbID()); // Pass IMDb ID to details screen
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size(); // Return total number of movies
    }

    // ViewHolder class to hold item views
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title, year;
        ImageView poster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views from item layout
            title = itemView.findViewById(R.id.movie_title);
            year = itemView.findViewById(R.id.movie_year);
            poster = itemView.findViewById(R.id.movie_poster);
        }
    }
}
