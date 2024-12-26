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
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ListMoviesActivity extends AppCompatActivity {

    private MoviesDatabase moviesDatabase;
    private MoviesAdapter adapter;
    private List<String> originalMovies; // Guardar la lista original

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_movies);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Establece la Toolbar como ActionBar

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        moviesDatabase = new MoviesDatabase(this);
        originalMovies = moviesDatabase.getAllMovies(); // Almacena la lista original
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> movies = new ArrayList<>(originalMovies); // Usa la lista original
        if (movies.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_movies_found), Toast.LENGTH_SHORT).show();
        }

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
        String actualTitle = extractTitle(movieTitle); // Extrae solo el título
        moviesDatabase.deleteMovie(actualTitle);
        Toast.makeText(this, getString(R.string.movie_deleted, actualTitle), Toast.LENGTH_SHORT).show();
        originalMovies = moviesDatabase.getAllMovies(); // Actualiza la lista original
        adapter.updateMovies(new ArrayList<>(originalMovies));
    }

    private String extractTitle(String movieTitle) {
        return movieTitle.replaceAll("\\s*\\(.*\\)$", "").trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_asc) {
            sortMovies(true);
            return true;
        } else if (id == R.id.sort_desc) {
            sortMovies(false);
            return true;
        } else if (id == R.id.clear_filters) {
            clearFilters();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void sortMovies(boolean ascending) {
        List<String> sortedMovies = new ArrayList<>(originalMovies);
        sortedMovies.sort((m1, m2) -> {
            float rating1 = extractRating(m1);
            float rating2 = extractRating(m2);
            return ascending ? Float.compare(rating1, rating2) : Float.compare(rating2, rating1);
        });
        adapter.updateMovies(sortedMovies);
    }

    private float extractRating(String movie) {
        String ratingPart = movie.replaceAll(".*\\((\\d+(\\.\\d+)?)\\)$", "$1");
        try {
            return Float.parseFloat(ratingPart);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    private void clearFilters() {
        adapter.updateMovies(new ArrayList<>(originalMovies));
    }
}
