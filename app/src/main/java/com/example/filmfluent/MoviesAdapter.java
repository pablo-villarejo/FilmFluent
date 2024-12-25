package com.example.filmfluent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<String> movies;
    private final OnMovieDeleteListener deleteListener;

    public MoviesAdapter(List<String> movies, OnMovieDeleteListener deleteListener) {
        this.movies = movies;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        String movie = movies.get(position);
        holder.movieTitle.setText(movie);
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDelete(movie));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateMovies(List<String> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle;
        Button deleteButton;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movie_title);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    interface OnMovieDeleteListener {
        void onDelete(String movieTitle);
    }
}
