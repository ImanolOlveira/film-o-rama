package com.imanololveira.filmorama.utils;

import android.database.Cursor;
import android.database.MatrixCursor;
import com.imanololveira.filmorama.R;
import com.imanololveira.filmorama.TmdbMovie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;

public class MovieDbJsonUtils {
    //The information is inside the results array in JSON
    final static String TMD_RESULTS = "results";
    //Error code
    final static String OWM_MESSAGE_CODE = "cod";

    /**
     * This method parses JSON from a web response and returns an array TmdbMovie objects
     *
     * @param movieJsonString JSON response from server
     * @return Array of TmdbMovie objects
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static TmdbMovie[] getMovieObjectFromJson(String movieJsonString)
            throws JSONException {

        //Original title
        final String TMD_TITLE = "original_title";
        //The movie ID
        final String TMD_MOVIE_ID = "id";
        //Release date
        final String TMD_RELEASE_DATE = "release_date";
        //Rating
        final String TMD_RATING = "vote_average";
        //Synopsis
        final String TMD_SYNOPSIS = "overview";
        // Poster path
        final String TMD_POSTER_PATH = "poster_path";

        JSONObject movieJson = new JSONObject(movieJsonString);

        //JSON error handler
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = movieJson.getJSONArray(TMD_RESULTS);
        //Builds TmdbMovie objects
        TmdbMovie[] movieList = new TmdbMovie[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the Strings from JSON
            JSONObject JsonMovie = moviesArray.getJSONObject(i);
            movieList[i] = new TmdbMovie();
            movieList[i].title = JsonMovie.getString(TMD_TITLE);
            movieList[i].movieId = JsonMovie.getString(TMD_MOVIE_ID);
            movieList[i].releaseDate = JsonMovie.getString(TMD_RELEASE_DATE);
            movieList[i].rating = JsonMovie.getString(TMD_RATING);
            movieList[i].synopsis = JsonMovie.getString(TMD_SYNOPSIS);
            movieList[i].posterPath = JsonMovie.getString(TMD_POSTER_PATH);
        }
        return movieList;
    }

    /**
     * This method parses JSON from a web response and returns a String containing al the reviews
     *
     * @param reviewsJson JSON response from server
     * @return A String containing the reviews
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String getReviewsTextFromJson(String reviewsJson) throws JSONException {
        String reviewString = "";

        //The information is inside the results array in JSON
        final String TMD_AUTHOR = "author";
        //Original title
        final String TMD_CONTENT = "content";
        JSONObject reviewJson = new JSONObject(reviewsJson);

        //JSON error handler
        if (reviewJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = reviewJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }
        JSONArray reviewsArray = reviewJson.getJSONArray(TMD_RESULTS);
        //Builds the review String
        TmdbMovie[] movieList = new TmdbMovie[reviewsArray.length()];
        for (int i = 0; i < reviewsArray.length(); i++) {
            // Get the Strings from JSON
            JSONObject JsonMovie = reviewsArray.getJSONObject(i);
            reviewString = reviewString +"- Comment by: " + JsonMovie.getString(TMD_AUTHOR) + "\n\n";
            reviewString = reviewString + JsonMovie.getString(TMD_CONTENT) + "\n\n\n";
        }
        return reviewString;
    }

    /**
     * This method parses JSON from a web response and returns a Cursor
     *
     * @param reviewsJson JSON response from server
     * @return A Cursor containing the trailer names and IDs
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Cursor getTrailerCursorFromJson(String reviewsJson) throws JSONException {
        MatrixCursor trailersCursor;

        //The information is inside the results array in JSON
        final String TMD_KEY = "key";
        //Original title
        final String TMD_NAME = "name";
        JSONObject reviewJson = new JSONObject(reviewsJson);

        //JSON error handler
        if (reviewJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = reviewJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }
        JSONArray reviewsArray = reviewJson.getJSONArray(TMD_RESULTS);
        String[] columns = new String[] {"_id","playIcon","trailerId", "name"};
        //The cursor to be returned
        trailersCursor = new MatrixCursor(columns);

        //Builds the review String
        for (int i = 0; i < reviewsArray.length(); i++) {
            // Get the Strings from JSON
            JSONObject JsonMovie = reviewsArray.getJSONObject(i);
            String trailerId =  JsonMovie.getString(TMD_KEY);
            String trailerName =  JsonMovie.getString(TMD_NAME);
            // Adds rows to the cursor
            trailersCursor.addRow(new Object[] {i,R.drawable.play_circle_outline,trailerId,trailerName});
        }
        return trailersCursor;
    }
}
