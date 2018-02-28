package com.imanololveira.filmorama;

import android.database.Cursor;

// Not included in the TmdbMovie class to avoid increasing the Parcelable boilerplate
//This way this info is packed for the Loader and ListView

public class TmdbExtras {
    public Cursor trailersCursor;
    public String reviews;
}
