package com.sss.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Class QueryPreferences.  Manages access to the application shared preference
 * files.  Note that these file physically exist in the sandbox.
 *
 * Created by Paul Shepherd on 7/14/2016.
 */
public class QueryPreferences
{
    private final static String KEY               = "QueryPreferences";
    private final static String PREF_SEARCH_QUERY = "searchQuery";


    /**
     * Get Flickr photo search string.  If no search string has been specified
     * then null is returned
     *
     * @param context
     * @return search string
     */
    public static String getStoredQuery(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    /**
     * Sets Flickr photo search string
     *
     * @param context
     * @param query
     */
    public static void setStoredQuery(Context context, String query)
    {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()         // get instance of the shared preference editor
                .putString(PREF_SEARCH_QUERY, query)
                .apply();       // make query setting visable
    }

}   // end public class QueryPreferences
