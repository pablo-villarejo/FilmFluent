package com.example.filmfluent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class AddMovieActivity extends AppCompatActivity {

    private MoviesDatabase moviesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database
        moviesDatabase = new MoviesDatabase(this);

        // UI elements
        EditText titleInput = findViewById(R.id.movie_title_input);
        SeekBar ratingSlider = findViewById(R.id.movie_rating_slider);
        TextView ratingValue = findViewById(R.id.movie_rating_value);
        Button saveButton = findViewById(R.id.save_movie_button);

        // Update rating dynamically
        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rating = progress / 2.0f; // Convert progress to 0.5 intervals
                ratingValue.setText(getString(R.string.rating) + rating + " / 5");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed
            }
        });

        // Save movie button action
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString().trim();
                float rating = ratingSlider.getProgress() / 2.0f;

                if (title.isEmpty()) {
                    Toast.makeText(AddMovieActivity.this, getString(R.string.please_enter_movie_title), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    moviesDatabase.addMovie(title, rating);
                    Toast.makeText(AddMovieActivity.this, getString(R.string.movie_added_successfully), Toast.LENGTH_SHORT).show();
                    titleInput.setText(""); // Clear title input
                    ratingSlider.setProgress(5); // Reset rating slider to default
                } catch (Exception e) {
                    Toast.makeText(AddMovieActivity.this, getString(R.string.error_adding_movie) + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
