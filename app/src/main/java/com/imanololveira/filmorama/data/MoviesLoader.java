package com.imanololveira.filmorama.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.imanololveira.filmorama.MainActivity;
import com.imanololveira.filmorama.TmdbMovie;
import static com.imanololveira.filmorama.data.MoviesDbHelper.getFavoritesList;
import static com.imanololveira.filmorama.utils.NetworksUtils.movieListFromBaseUrl;

public class MoviesLoader extends AsyncTaskLoader<TmdbMovie[]> {

    // Loader Ids
    public static final int LOADER_FAV = 0;
    public static final int LOADER_POP = 1;
    public static final int LOADER_TOP = 2;
    //The base URLs used to build the query string.
    private final static String POP_BASE_URL = "http://api.themoviedb.org/3/movie/popular";
    private final static String TOP_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated";

    private int mLoaderId;


    public MoviesLoader(Context context, int loaderId) {
        super(context);
        // getContext() is used to prevent Activity's context to leak.
        mLoaderId = loaderId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Performs asynchronous loading of data
    @Override
    public TmdbMovie[] loadInBackground() {
        TmdbMovie[] mFavoritesListData = getFavoritesList(getContext());
        MainActivity.favoritesListData = mFavoritesListData;
        // internet remote queries to get the different lists
        switch (mLoaderId){
            case LOADER_FAV:
                // Content provider query to refresh the favorite list
                return mFavoritesListData;
            case LOADER_POP:
                return movieListFromBaseUrl(POP_BASE_URL);
            case LOADER_TOP:
                return movieListFromBaseUrl(TOP_BASE_URL);
            default:
                return null;
        }
    }

    // Sends the result of the load to the listener
    @Override
    public void deliverResult(TmdbMovie[] data) {
            super.deliverResult(data);
    }
}
