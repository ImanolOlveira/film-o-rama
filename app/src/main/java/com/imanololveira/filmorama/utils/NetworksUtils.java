package com.imanololveira.filmorama.utils;

import android.database.Cursor;
import android.net.Uri;

import com.imanololveira.filmorama.TmdbMovie;

import org.json.JSONException;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.imanololveira.filmorama.utils.MovieDbJsonUtils.getReviewsTextFromJson;
import static com.imanololveira.filmorama.utils.MovieDbJsonUtils.getMovieObjectFromJson;


public class NetworksUtils {

    //API KEY
    private static String mApiKey;
    private final static String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String POSTER_BASE_URL ="http://image.tmdb.org/t/p/";
    private final static String PARAM_QUERY = "api_key";

    /**
     * Sets the Tmdb API key.
     *
     * @param apiKey The Tmdb API key String.
     */

    public static void setApiKey(String apiKey){
        mApiKey = apiKey;
    }

    /**
     * Builds the URL to query the movie list.
     *
     * @param QueryBaseUrl The base URL for TMDb query.
     * @return The URL to use to query TMDb.
     */
    public static URL buildListUrl(String QueryBaseUrl) {
        Uri builtUri = Uri.parse(QueryBaseUrl).buildUpon()
                .appendQueryParameter(PARAM_QUERY, mApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL to get the poster.
     *
     * @param PosterPath The base URL for TMDb query.
     * @return The URL string to use to query TMDb.
     */
    public static String buildPosterQueryUrl(String PosterPath, String PosterWidth){
        String url;
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(PosterWidth)
                .appendEncodedPath(PosterPath)
                .build();
        url = builtUri.toString();
        return url;
    }

    /**
     * Builds the URL to get the reviews.
     *
     * @param movieId Movie id needed to get the reviews.
     * @return The URL string to use to query TMDb.
     */
    private static URL buildReviewsQueryUrl(String movieId){
        URL url;
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(PARAM_QUERY, mApiKey)

                .build();
        url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL to get the trailers.
     *
     * @param movieId Movie id needed to get the reviews.
     * @return The URL string to use to query TMDb.
     */
    private static URL buildTrailersQueryUrl(String movieId){
        URL url;
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(PARAM_QUERY, mApiKey)
                .build();
        url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }catch(IOError error){
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method returns a TMdb objetc from a base URL. Returns null if something goes wrong
     *
     * @param baseUrl The URL to fetch the HTTP response from.
     * @return TMdb objetc / null
     */
    public static TmdbMovie[] movieListFromBaseUrl(String baseUrl){
        URL queryUrl = buildListUrl(baseUrl);
        String SearchResults;
        try {
            SearchResults = getResponseFromHttpUrl(queryUrl);
        } catch (IOException e) { //Get response exception
            e.printStackTrace();
            return null;
        }
        if (SearchResults != null && !SearchResults.equals("")) {
            try {
                // Method to parse the Json String
                return getMovieObjectFromJson(SearchResults);
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns a String from a movie ID. Returns null if something goes wrong
     *
     * @param movieId The parameter for the HTTP query.
     * @return String containing all the reviews / null
     */
    public static String reviewsTextFromId(String movieId){
        URL queryUrl = buildReviewsQueryUrl(movieId);
        String reviewsJson;
        try {
            reviewsJson = getResponseFromHttpUrl(queryUrl);
        } catch (IOException e) { //Get response exception
            e.printStackTrace();
            return null;
        }
        if (reviewsJson != null && !reviewsJson.equals("")) {
            try {
                // Method used to parse the Json String
                return getReviewsTextFromJson(reviewsJson);
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns a Cursor from a movie ID.
     * Returns null if something goes wrong
     *
     * @param movieId The parameter for the HTTP query.
     * @return Cursor containing all the trailer IDs and names / null
     */
    public static Cursor trailersCursorFromId(String movieId){
        URL queryUrl = buildTrailersQueryUrl(movieId);
        String trailersJson;
        try {
            trailersJson = getResponseFromHttpUrl(queryUrl);
        } catch (IOException e) { //Get response exception
            e.printStackTrace();
            return null;
        }
        if (trailersJson != null && !trailersJson.equals("")) {
            try {
                return MovieDbJsonUtils.getTrailerCursorFromJson(trailersJson);
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}
