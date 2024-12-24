package com.example.filmfluent;

import android.provider.BaseColumns;

public final class MoviesContract {
    private MoviesContract() {}

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "Movies";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_RATING = "rating";
    }
}
