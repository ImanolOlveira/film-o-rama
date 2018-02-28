package com.imanololveira.filmorama.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import com.imanololveira.filmorama.TmdbExtras;
import com.imanololveira.filmorama.TmdbMovie;
import static com.imanololveira.filmorama.utils.NetworksUtils.reviewsTextFromId;
import static com.imanololveira.filmorama.utils.NetworksUtils.trailersCursorFromId;

public class DetailLoader extends AsyncTaskLoader<TmdbExtras> {

    // The loader ID
    public static final int LOADER_GET_DETAILS = 10;
    public static final int LOADER_ADD_FAVORITE = 11;
    public static final int LOADER_DEL_FAVORITE = 12;

    private int mLoaderId;
    private TmdbMovie mSelectedMovie;

    public DetailLoader(Context context, int loaderId, TmdbMovie selectedMovie) {
        super(context);
        mLoaderId = loaderId;
        mSelectedMovie = selectedMovie;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Performs asynchronous loading of data
    @Override
    public TmdbExtras loadInBackground() {
        // Query and load the reviews and the movie trailers in the background
        switch (mLoaderId){

            case LOADER_GET_DETAILS:
                // A TmdbExtras object is used to store the details
                TmdbExtras movieDetails = new TmdbExtras();
                // Some method are called to start the queries
                movieDetails.reviews = reviewsTextFromId(mSelectedMovie.movieId);
                movieDetails.trailersCursor = trailersCursorFromId(mSelectedMovie.movieId);
                return movieDetails;

            case LOADER_ADD_FAVORITE:
                // Create new empty ContentValues object
                ContentValues contentValues = new ContentValues();
                // Put the task description and selected mPriority into the ContentValues
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mSelectedMovie.title);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mSelectedMovie.movieId);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, mSelectedMovie.releaseDate);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, mSelectedMovie.rating);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, mSelectedMovie.synopsis);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, mSelectedMovie.posterPath);

                // Insert the content values via a ContentResolver
                Uri insertUri = getContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
                return null;

            case LOADER_DEL_FAVORITE:
                // Build appropriate uri with String row id appended
                Uri delUri = MoviesContract.MoviesEntry.CONTENT_URI;
                delUri = delUri.buildUpon().appendPath(mSelectedMovie.movieId).build();

                // Delete a single row of data using a ContentResolver
                int deletedRowsCount = getContext().getContentResolver().delete(delUri, null, null);
                return null;

            default:
                return null;
        }
    }

    // Sends the result of the load to the listener
    @Override
    public void deliverResult(TmdbExtras data) {
        if (data != null){
            super.deliverResult(data);
        }
    }
}
