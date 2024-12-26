package com.example.filmfluent;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MoviesDatabase {

    private SQLiteDatabase db;

    public MoviesDatabase(Context context) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void addMovie(String title, float rating) {
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_NAME_TITLE, title);
        values.put(MoviesContract.MovieEntry.COLUMN_NAME_RATING, rating);
        db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
    }

    public List<String> getAllMovies() {
        List<String> movies = new ArrayList<>();
        String[] columns = {
                MoviesContract.MovieEntry._ID,
                MoviesContract.MovieEntry.COLUMN_NAME_TITLE,
                MoviesContract.MovieEntry.COLUMN_NAME_RATING
        };
        Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME, columns, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_TITLE));
                @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_RATING));
                movies.add(title + " (" + rating + ")");
            }
        } finally {
            cursor.close();
        }
        return movies;
    }

    @SuppressLint("Range")
    public float getMovieRating(String title) {
        float rating = -1;
        String[] columns = {
                MoviesContract.MovieEntry.COLUMN_NAME_RATING
        };
        String selection = MoviesContract.MovieEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { title };
        Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                rating = cursor.getFloat(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_NAME_RATING));
            }
        } finally {
            cursor.close();
        }
        return rating;
    }

    public void updateMovieRating(String title, float newRating) {
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_NAME_RATING, newRating);
        String where = MoviesContract.MovieEntry.COLUMN_NAME_TITLE + " = ?";
        String[] whereArgs = { title };
        db.update(MoviesContract.MovieEntry.TABLE_NAME, values, where, whereArgs);
    }

    public void deleteMovie(String title) {
        String where = MoviesContract.MovieEntry.COLUMN_NAME_TITLE + " = ?";
        String[] whereArgs = { title };
        db.delete(MoviesContract.MovieEntry.TABLE_NAME, where, whereArgs);
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
        super.finalize();
    }
}
