package com.imanololveira.filmorama.data;

import com.imanololveira.filmorama.TmdbMovie;
import com.imanololveira.filmorama.data.MoviesContract.MoviesEntry;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "moviesDb.db";
    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;

    // Constructor
    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create movies table
        final String CREATE_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * A method to get the favorite movies IDs and transforms the cursor into a TmdbMovie object
     */
    public static TmdbMovie[] getFavoritesList(Context context){
        try {
            Cursor cusorFromQuery = context.getContentResolver().query(MoviesContract
                            .MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MoviesContract.MoviesEntry.COLUMN_TITLE);
            return TmdbMovie.movieListFromCursor(cusorFromQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

