package com.sss.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Paul Shepherd on 7/12/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread
{
    private final static String TAG = "ThumbnailDownloader";
    private final static int    MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;


    public interface ThumbnailDownloadListener<T>
    {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }


    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener)
    {
        mThumbnailDownloadListener = listener;
    }

    /**
     * Constructor
     */
    public ThumbnailDownloader(Handler responseHandler)
    {
        super(TAG);
        mResponseHandler = responseHandler;
    }


    /**
     *
     */
    @Override
    protected void onLooperPrepared()
    {
        mRequestHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == MESSAGE_DOWNLOAD)
                {
                    T target = (T)msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }


    /**
     * Implements Thumb nail download queue
     *
     * @param target
     * @param url URL of the photograph thumbnail
     */
    public void queueThumbnail(T target, String url)
    {
        Log.i(TAG, "Got a URL: " + url);

        if(url == null)
        {
            mRequestMap.remove(target);
        }
        else
        {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }   // end public void queueThumbnail(T target, String url)


    public void clearQueue()
    {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }


    /**
     * Helper method where the donwloading happens.
     * @param target
     */
    private void handleRequest(final T target)
    {
        try
        {
            final String url = mRequestMap.get(target);

            if(url == null)
            {
                return;
            }

            byte[] bitmapBytes  = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable()
            {
                public void run()
                {
                    if(mRequestMap.get(target) != url)
                    {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        }
        catch(IOException ioe)
        {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }   // end private void handleRequest(final T target)
}   // end public class ThumbnailDownloader<T> extends HandlerThread

