package com.example.filmfluent;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddMovieActivity extends AppCompatActivity {
    private MoviesDatabase moviesDatabase;

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=";

    private EditText titleInput;
    private GridLayout movieCardsContainer;
    private RatingBar ratingBar;
    private Button searchButton;
    private Button saveButton;
    private boolean isMovieValid = false; // To track if the selected movie is valid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        moviesDatabase = new MoviesDatabase(this);

        // Initialize UI elements
        titleInput = findViewById(R.id.movie_title_input);
        movieCardsContainer = findViewById(R.id.movie_cards_container);
        ratingBar = findViewById(R.id.movie_rating_bar);
        searchButton = findViewById(R.id.search_movie_button);
        saveButton = findViewById(R.id.save_movie_button);

        // Set up Search Button
        searchButton.setOnClickListener(v -> {
            String query = titleInput.getText().toString().trim();
            if (!query.isEmpty()) {
                new SearchMoviesTask().execute(query);
            } else {
                Toast.makeText(AddMovieActivity.this, "Please enter a movie title", Toast.LENGTH_SHORT).show();
            }
        });

        // Save button action
        saveButton.setOnClickListener(v -> {
            if (isMovieValid) {
                String title = titleInput.getText().toString().trim();
                float rating = ratingBar.getRating();

                if (title.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, "Please select a movie", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show confirmation dialog before saving
                new AlertDialog.Builder(AddMovieActivity.this)
                        .setTitle("Confirm Save")
                        .setMessage("Do you want to save the movie \"" + title + "\" with the rating " + rating + "?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Save the movie to the database
                            try {
                                moviesDatabase.addMovie(title, rating);
                                Toast.makeText(AddMovieActivity.this, "Movie added successfully", Toast.LENGTH_SHORT).show();
                                titleInput.setText(""); // Clear title input
                                ratingBar.setRating(0); // Reset rating bar
                            } catch (Exception e) {
                                Toast.makeText(AddMovieActivity.this, "Error adding movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Close the dialog without doing anything
                            dialog.dismiss();
                        })
                        .show();
            } else {
                Toast.makeText(AddMovieActivity.this, "Please select a valid movie from the list", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // AsyncTask for searching movies by title
    private class SearchMoviesTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            String query = params[0];
            JSONArray movies = null;

            try {
                // Prepare the URL with the query
                URL url = new URL(BASE_URL + query);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                StringBuilder response = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    response.append((char) data);
                    data = reader.read();
                }

                // Parse the response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                movies = jsonResponse.getJSONArray("results");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(JSONArray movies) {
            if (movies != null && movies.length() > 0) {
                // Clear previous movie cards
                movieCardsContainer.removeAllViews();

                // Add each movie card dynamically to the GridLayout
                for (int i = 0; i < movies.length(); i++) {
                    try {
                        JSONObject movie = movies.getJSONObject(i);
                        String title = movie.getString("title");
                        String posterPath = movie.getString("poster_path");

                        // Inflate new movie card
                        View movieCard = getLayoutInflater().inflate(R.layout.movie_card, null);
                        ImageView moviePoster = movieCard.findViewById(R.id.movie_poster);
                        TextView movieTitle = movieCard.findViewById(R.id.movie_title);

                        // Set movie title and poster
                        movieTitle.setText(title);
                        Glide.with(AddMovieActivity.this)
                                .load("https://image.tmdb.org/t/p/w500" + posterPath)
                                .into(moviePoster);

                        // Set click listener for each movie card
                        movieCard.setOnClickListener(v -> {
                            // Set the selected movie as valid and update the input field
                            titleInput.setText(title);
                            isMovieValid = true;
                        });

                        // Add the movie card to the GridLayout
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = 0; // Let the GridLayout distribute the width
                        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Equal width
                        movieCard.setLayoutParams(params);

                        movieCardsContainer.addView(movieCard);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(AddMovieActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
