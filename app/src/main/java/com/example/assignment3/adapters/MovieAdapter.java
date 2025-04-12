package com.example.assignment3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment3.MovieDetailsActivity;
import com.example.assignment3.R;
import com.example.assignment3.models.MovieModel;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnItemClick {
        void onClick(MovieModel movie);
    }

    private Context context;
    private List<MovieModel> movieList;
    private OnItemClick listener;

    // Constructor with custom click listener
    public MovieAdapter(Context context, List<MovieModel> movieList, OnItemClick listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    // Default constructor (for MainActivity use)
    public MovieAdapter(Context context, List<MovieModel> movieList) {
        this.context = context;
        this.movieList = movieList;
        this.listener = null;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieModel movie = movieList.get(position);

        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());

        Glide.with(holder.poster.getContext())
                .load(movie.getPoster())
                .placeholder(R.drawable.placeholder)
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(movie); // Custom handler (e.g., open FavoriteDetailsActivity)
            } else {
                // Default behavior
                context.startActivity(
                        new android.content.Intent(context, MovieDetailsActivity.class)
                                .putExtra("imdbId", movie.getImdbID()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title, year;
        ImageView poster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movie_title);
            year = itemView.findViewById(R.id.movie_year);
            poster = itemView.findViewById(R.id.movie_poster);
        }
    }
}
