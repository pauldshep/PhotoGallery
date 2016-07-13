package com.sss.android.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Paul Shepherd on 7/12/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread
{
    private final static String TAG = "ThumbnailDownloader";

    /**
     * Constructor
     */
    public ThumbnailDownloader()
    {
        super(TAG);
    }


    /**
     * Implements Thumbnail download queue
     *
     * @param target
     * @param url
     */
    public void queueThumbnail(T target, String url)
    {
        Log.i(TAG, "Got a URL: " + url);
    }
}

