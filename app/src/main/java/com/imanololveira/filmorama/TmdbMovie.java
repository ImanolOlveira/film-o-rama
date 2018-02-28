package com.imanololveira.filmorama;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.imanololveira.filmorama.data.MoviesContract;

/**
 * The parcelable object representing a movie
 */
public class TmdbMovie implements Parcelable {

    public String title;
    public String movieId;
    public String releaseDate;
    public String rating;
    public String synopsis;
    public String posterPath;

    public TmdbMovie() {
    }

    public static TmdbMovie[] movieListFromCursor(Cursor cursor){
        TmdbMovie[] movieList = new TmdbMovie[cursor.getCount()];
        // Indices for the _id, description, and priority columns
        int idTitle = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
        int idMovieId = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
        int idReleaseDate = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
        int idRating = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING);
        int idSynopsis = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS);
        int idPosterPath = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH);

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i); // get to the right location in the cursor
            movieList[i] = new TmdbMovie();
            // Determine the values of the wanted data
            movieList[i].title = cursor.getString(idTitle);
            movieList[i].movieId = cursor.getString(idMovieId);
            movieList[i].releaseDate = cursor.getString(idReleaseDate);
            movieList[i].rating = cursor.getString(idRating);
            movieList[i].synopsis = cursor.getString(idSynopsis);
            movieList[i].posterPath = cursor.getString(idPosterPath);

        }
        return movieList;
    }

    /**
     * Method to check if a TmdbMovie is in a TmdbMovie array. Movie id's are compared.
     *
     * @param selectedMovie A TmdbMovie.
     * @param favList A TmdbMovie array.
     */
    public static boolean isFavorite(TmdbMovie selectedMovie,TmdbMovie[] favList){
        if(favList != null && favList.length > 0){
            for(TmdbMovie movieItem: favList){
                if(movieItem.movieId.equals(selectedMovie.movieId))
                    return true;
            }
            return false;
        }else{
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.movieId);
        dest.writeString(this.releaseDate);
        dest.writeString(this.rating);
        dest.writeString(this.synopsis);
        dest.writeString(this.posterPath);
    }

    protected TmdbMovie(Parcel in) {
        this.title = in.readString();
        this.movieId = in.readString();
        this.releaseDate = in.readString();
        this.rating = in.readString();
        this.synopsis = in.readString();
        this.posterPath = in.readString();
    }

    public static final Creator<TmdbMovie> CREATOR = new Creator<TmdbMovie>() {
        @Override
        public TmdbMovie createFromParcel(Parcel source) {
            return new TmdbMovie(source);
        }

        @Override
        public TmdbMovie[] newArray(int size) {
            return new TmdbMovie[size];
        }
    };
}
