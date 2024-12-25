package com.example.filmfluent;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListMoviesActivity extends AppCompatActivity {

    private MoviesDatabase moviesDatabase;
    private MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_movies);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        moviesDatabase = new MoviesDatabase(this, "movies_db");
        List<String> movies = moviesDatabase.getAllMovies();
        Toast.makeText(this, "Movies: " + movies.toString(), Toast.LENGTH_LONG).show();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> movies = moviesDatabase.getAllMovies();
        adapter = new MoviesAdapter(movies, this::showDeleteConfirmationDialog);
        recyclerView.setAdapter(adapter);

    }

    private void showDeleteConfirmationDialog(String movieTitle) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_movie))
                .setMessage(getString(R.string.confirm_delete_movie, movieTitle))
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteMovie(movieTitle))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteMovie(String movieTitle) {
        moviesDatabase.deleteMovie(movieTitle);
        Toast.makeText(this, getString(R.string.movie_deleted, movieTitle), Toast.LENGTH_SHORT).show();
        adapter.updateMovies(moviesDatabase.getAllMovies());
    }
}
