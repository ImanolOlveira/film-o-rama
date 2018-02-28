package com.imanololveira.filmorama;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imanololveira.filmorama.data.DetailLoader;
import com.squareup.picasso.Picasso;

import static com.imanololveira.filmorama.data.DetailLoader.LOADER_ADD_FAVORITE;
import static com.imanololveira.filmorama.data.DetailLoader.LOADER_DEL_FAVORITE;
import static com.imanololveira.filmorama.data.DetailLoader.LOADER_GET_DETAILS;
import static com.imanololveira.filmorama.utils.NetworksUtils.buildPosterQueryUrl;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<TmdbExtras>{

    //The Views that will show the data.
    private TmdbMovie selectedMovie;
    private ImageView movieThumb;
    private TextView movieName;
    private TextView movieReleaseDate;
    private TextView movieRating;
    private TextView movieSynopsis;
    private TextView reviewsTextView;
    private Button favoriteButton;
    private TextView trailersLabel;
    private TextView reviewsLabel;
    private View sectionSeparator;
    private String MARK_AS_FAV;
    private String UNMARK_AS_FAV;
    private ListView trailerList;
    // A header and a footer are used with the listView. This way is not used inside a ScrollView
    private View header;
    private View footer;

    boolean isFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        trailerList = (ListView) findViewById(R.id.trailers_list_view);

        // Starts an adapter using a fake cursor. This way a null exception is avoided in the loader
        String[] fromColumns = {"playIcon","name"};
        String[] columns = new String[] {"_id","playIcon","trailerId", "name"};
        int[] toViews = {R.id.play_imageView,R.id.trailer_name_text_view};
        Cursor dummyCursor = new MatrixCursor(columns);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getBaseContext(), R.layout.trailer_list_item, dummyCursor, fromColumns, toViews, 0);

        //Inflates the header and the footer. Then, both are attached to the adapter
        header = View.inflate(this, R.layout.activity_detail_header, null);
        footer = View.inflate(this, R.layout.activity_detail_footer, null);
        trailerList.addHeaderView(header,null,false);
        trailerList.addFooterView(footer,null,false);
        trailerList.setAdapter(adapter);

        // The listener for the list
        trailerList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick (AdapterView < ? > adapter, View view,int position, long arg){
                        //Gets the video ID from the selected object
                        Cursor cursor = (Cursor) adapter.getAdapter().getItem(position);
                        String trailerId = cursor.getString(cursor.getColumnIndex("trailerId"));

                        // Creates the Intent
                        Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + trailerId);
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW, videoUri);

                        // Checks if the system can start an Intent to show a YouTube video
                        if (videoIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(videoIntent); // Starts the Intent
                        }
                    }
                 }
        );

        // Initializes the text constants related to the button
        MARK_AS_FAV = getString(R.string.button_favorite);
        UNMARK_AS_FAV = getString(R.string.button_not_favorite);

        // Link the rest of the views
        movieName = (TextView) findViewById(R.id.name_text_view);
        movieReleaseDate = (TextView) findViewById(R.id.release_date_text_view);
        movieRating = (TextView) findViewById(R.id.rating_text_view);
        movieSynopsis = (TextView) findViewById(R.id.synopsis_text_view);
        movieThumb = (ImageView) findViewById(R.id.thumbnail_imageView);
        favoriteButton = (Button) findViewById(R.id.favorites_button);
        reviewsLabel = (TextView) findViewById(R.id.reviews_label);
        trailersLabel = (TextView) findViewById(R.id.trailers_label);
        reviewsTextView = (TextView) findViewById(R.id.reviews_text_view);
        sectionSeparator = findViewById(R.id.separator);

        // If no data is received in the loader, the labels are not needed
        reviewsLabel.setVisibility(View.GONE);
        trailersLabel.setVisibility(View.GONE);
        sectionSeparator.setVisibility(View.GONE);

        //Receives the Intent from MainActivity.
        Intent intentThatStartedThisActivity = getIntent();

        // Fill the content of the Views with the data passed in the Intent(if it is not empty).
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                //Gets the parcelable TmdbMovie object from the Intent.
                selectedMovie = (TmdbMovie) intentThatStartedThisActivity
                        .getParcelableExtra(Intent.EXTRA_TEXT);
                isFavorite = intentThatStartedThisActivity.getBooleanExtra("IS_FAVORITE",false);
                if (isFavorite) {
                    favoriteButton.setText(UNMARK_AS_FAV);
                }else{
                    favoriteButton.setText(MARK_AS_FAV);

                }
                //Fills the views with data.
                movieName.setText(selectedMovie.title);
                movieReleaseDate.setText(selectedMovie.releaseDate.substring(0,4));
                movieRating.setText(selectedMovie.rating + "/10");
                movieSynopsis.setText(selectedMovie.synopsis);

                //Gets the poster thumbnail and sets the corresponding ImageView content.
                Picasso.with(this)
                        .load(buildPosterQueryUrl(selectedMovie.posterPath, "w342"))
                        .error( R.drawable.picasso_error)
                        .into(movieThumb);
            }

            //Start a new thread: get the movie list data. Bundle param is null.
            getSupportLoaderManager().initLoader(LOADER_GET_DETAILS,null , this);
        }
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a text or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data.
     */
    @Override
    public Loader<TmdbExtras> onCreateLoader(final int loaderId, final Bundle loaderArgs) {

        return new DetailLoader(this, loaderId, selectedMovie);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<TmdbExtras> loader, TmdbExtras data) {

        // Set the adapter data using setAdapterData method
        if (data.trailersCursor != null) {
            if (data.reviews != "") {
                // We have data. Labels are needed.
                reviewsLabel.setVisibility(View.VISIBLE);
                sectionSeparator.setVisibility(View.VISIBLE);
                reviewsTextView.setText(data.reviews);
            }

            if (data.trailersCursor.getCount() > 0) {
                // We have data. Labels are needed.
                trailersLabel.setVisibility(View.VISIBLE);
                sectionSeparator.setVisibility(View.VISIBLE);
                // Gets data from cursor
                String[] fromColumns = {"playIcon", "name"};
                int[] toViews = {R.id.play_imageView, R.id.trailer_name_text_view};
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getBaseContext()
                        , R.layout.trailer_list_item
                        , data.trailersCursor
                        , fromColumns, toViews, 0);
                trailerList.setAdapter(adapter);
            }
        }
    }

    /**
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<TmdbExtras> loader) {
        // Not implemented;
    }

    /**
     * Attach an OnClickListener to it, so that when it's clicked, a new entry is made trough a
     * Content Provider
     */
    public void onClickFavorites(View view){

        if(favoriteButton.getText().toString() == MARK_AS_FAV){ //ADD a movie to favorites
            favoriteButton.setText(UNMARK_AS_FAV);
            getSupportLoaderManager().initLoader(LOADER_ADD_FAVORITE,null , this);

        }else{ //DELETE a movie from favorites
            favoriteButton.setText(MARK_AS_FAV);
            getSupportLoaderManager().initLoader(LOADER_DEL_FAVORITE,null , this);
        }
    }
}
