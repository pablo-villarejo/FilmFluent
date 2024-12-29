package com.example.filmfluent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomMovieActivity extends AppCompatActivity {

    private TextView movieTitleText;
    private ImageView moviePosterImage;
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_movie);

        movieTitleText = findViewById(R.id.movie_title);
        moviePosterImage = findViewById(R.id.movie_poster);

        Button randomMovieButton = findViewById(R.id.random_movie_button);
        randomMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Every time the button is clicked, fetch a new random movie
                new FetchRandomMovieTask().execute();
            }
        });
    }

    private class FetchRandomMovieTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            String[] movieDetails = new String[2]; // [0] = title, [1] = poster_url
            try {
                // Create URL to fetch popular movies
                // Tried to use a library for this but its too much
                // so im just using javas native implementation
                URL url = new URL(BASE_URL + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read the response from the API
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                StringBuilder response = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    response.append((char) data);
                    data = reader.read();
                }

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");

                // Get a random movie from the list
                int randomIndex = (int) (Math.random() * results.length());
                JSONObject movie = results.getJSONObject(randomIndex);
                movieDetails[0] = movie.getString("title"); // Movie title
                movieDetails[1] = "https://image.tmdb.org/t/p/w500" + movie.getString("poster_path"); // Poster URL

            } catch (Exception e) {
                Log.e("RandomMovieActivity", "Error fetching movie", e);
            }

            return movieDetails;
        }

        @Override
        protected void onPostExecute(String[] movieDetails) {
            // Update the UI with the fetched movie details
            if (movieDetails[0] != null && movieDetails[1] != null) {
                movieTitleText.setText(movieDetails[0]);

                // Use Glide to load the image into the ImageView
                Glide.with(RandomMovieActivity.this)
                        .load(movieDetails[1])
                        .into(moviePosterImage);
            }
        }
    }
}
