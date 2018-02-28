package com.imanololveira.filmorama;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import static com.imanololveira.filmorama.utils.NetworksUtils.buildPosterQueryUrl;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    public static TmdbMovie[] moviesDataList;
    private static int posterWidth;
    private static int posterHeight;
    private static String posterQueryWidth;
    private static final double MAX_INTERPOLATION = 1.5;

    /**
    * An on-click handler that we've defined to make it easy for an Activity to interface with
    * our RecyclerView
    */
    private final MoviesAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(int adapterPosition);
    }

    /**
     * Creates a MoviesAdapter. In the process, a method is called to calculate the poster dimensions.
     *
     * //@param movieList TmdbMovie object array that contains the data.
     * @param screenWidth The screen horizontal size in pixels.
     * @param clickHandler The on-click handler for this adapter.
     */

    public MoviesAdapter(int screenWidth,
                         MoviesAdapterOnClickHandler clickHandler){

        mClickHandler = clickHandler;
        calculatePosterDimensions(screenWidth, MAX_INTERPOLATION);
    }

    /**
     * Calculates the poster dimensions.
     * Calculates the variable posterQueryWidth, that is used to set the size of the poster in
     * the query, given a screen size and factor that controls the maximum interpolation allowed .
     *
     * @param pixels The screen size in pixels.
     * @param maxInterpolation The maximum interpolation allowed. A factor of 2 means that the
     *                         image will be stretched to twice its size.
     */
    private void calculatePosterDimensions(int pixels, double maxInterpolation){
        posterWidth = pixels/2;
        posterHeight = (int) (posterWidth *1.5); //They always have an aspect ratio of 1.5

        //Different Strings are selected depending on the poster size
        if (posterWidth>780*2){
            posterQueryWidth = "original";
        }else if (500* maxInterpolation < posterWidth && posterWidth <= 780* maxInterpolation){
            posterQueryWidth = "w780";
        }else if (342* maxInterpolation < posterWidth && posterWidth <= 500* maxInterpolation){
            posterQueryWidth = "w500";
        }else if (185* maxInterpolation < posterWidth && posterWidth <= 342* maxInterpolation){
            posterQueryWidth = "w342";
        }else if (154* maxInterpolation < posterWidth && posterWidth <= 185* maxInterpolation){
            posterQueryWidth = "w185";
        }else if (92* maxInterpolation < posterWidth && posterWidth <= 154* maxInterpolation){
            posterQueryWidth = "w154";
        }else{
            posterQueryWidth = "w92";
        }
    }

    /**
     * The ViewHolder. Represents a single item.
     */
    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The ImageView used to show the poster
        private final ImageView mThumbView;

        // Constructor with the corresponding listener attached.
        public MoviesAdapterViewHolder(View view) {
            super(view);
            mThumbView = (ImageView) view.findViewById(R.id.thumbnail_view);
            view.setOnClickListener(this);
        }

        /**
         * onClick override. Passes the clicked item's data to the handler
         *
         * @param v The View
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

    /**
     * Called when each new ViewHolder is created when the RecyclerView is laid out
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  ViewType integer to provide a different layout.
     * @return A new MovieAdapterViewHolder.
     */

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    /**
     * Called by the RecyclerView to fill the ViewHolder's ImageView with data at the given position
     * and the help of the Picasso library.
     *
     * @param moviesAdapterViewHolder The ViewHolder.
     * @param position                The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        String queryUrl = buildPosterQueryUrl(moviesDataList[position].posterPath, posterQueryWidth);
        // Resize is needed because the ImageView's height is set up before center_crop
        // interpolation. As a result, the image gets cropped.
        Picasso.with(moviesAdapterViewHolder.mThumbView.getContext())
                .load(queryUrl)
                .resize(posterWidth,posterHeight)
                .error( R.drawable.picasso_error)
                .into(moviesAdapterViewHolder.mThumbView);
    }

    /**
     * Gets the number of movies in the list
     *
     * @return The number of items
     */

    @Override
    public int getItemCount() {
        if (null == moviesDataList) return 0;
        return moviesDataList.length;
    }

    public void setAdapterData(TmdbMovie[] incomingData){
        moviesDataList = incomingData;
        notifyDataSetChanged();
    }
}


