package com.sss.android.photogallery;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class FlickrFetchr
 *
 * Created by Paul Shepherd on 7/10/2016.
 */
public class FlickrFetchr
{
    private final static String TAG     = "FlickrFetchr";
    private final static String API_KEY = "5a8c81f7f3d10ff213e92413ecec51ab";

    public byte[] getUrlBytes(String urlSpec) throws IOException
    {
        Log.i(TAG, "getUrlBytes: urlSpec = " + urlSpec);

        URL               url        = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream           in  = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int    bytesRead = 0;
            byte[] buffer    = new byte[1024];

            while((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        }
        finally
        {
            connection.disconnect();
        }
    }


    public String getUrlString(String urlSpec) throws IOException
    {
        return new String(getUrlBytes(urlSpec));
    }


    public void fetchItems()
    {
        try
        {

            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i( TAG, "Received JSON: " + jsonString);
        }
        catch(IOException ioe)
        {
            Log.e( TAG, "Failed to fetch items", ioe);
        }
    }   // end public void fetchItems()

}   // end public class FlickrFetchr


