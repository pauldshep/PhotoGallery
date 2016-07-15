package com.sss.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class FlickrFetchr.  Downloads pictures from Flickr
 *
 * Created by Paul Shepherd on 7/10/2016.
 */
public class FlickrFetchr
{
    private final static String TAG     = "FlickrFetchr";
    private final static String API_KEY = "5a8c81f7f3d10ff213e92413ecec51ab";
    private final static String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private final static String SEARCH_METHOD = "flickr.photos.search";
    private final static Uri    ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();


    /**
     * Get bytes from specified URL and return them to the calling function
     *
     * @param urlSpec URL to get bytes from
     * @return
     * @throws IOException
     */
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
    }   // end public byte[] getUrlBytes(String urlSpec) throws IOException


    /**
     * Converts bytes returned from the URL into a string
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException
    {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos()
    {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }


    public List<GalleryItem> searchPhotos(String query)
    {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }


    /**
     * Builds REST request for FLICKR URL for recient photos that have been
     * uploaded.  The request is sent to the URL and photo information is
     * returned.
     * @param url
     */
    private List<GalleryItem> downloadGalleryItems(String url)
    {
        List<GalleryItem> items = new ArrayList<>();

        try
        {
//            // build REST request for flickr
//            String url = Uri.parse("https://api.flickr.com/services/rest/")
//                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter("format", "json")
//                    .appendQueryParameter("nojsoncallback", "1")
//                    .appendQueryParameter("extras", "url_s")
//                    .build().toString();

            // get information returned from URL as a string
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);

            // create JSON object from the string returned from the URL
            JSONObject jsonBody = new JSONObject(jsonString);

            // parse JSON string
            parseItems(items, jsonBody);
        }
        catch(JSONException je)
        {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        catch(IOException ioe)
        {
            Log.e( TAG, "Failed to fetch items", ioe);
        }

        return items;
    }   // end public void fetchItems()


    /**
     * Appends the necesary parameters.  It dynamically fills in the method
     * parameter value.
     */
    private String buildUrl(String method, String query)
    {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);

        if(method.equals(SEARCH_METHOD))
        {
            uriBuilder.appendQueryParameter("text", query);
        }

        return uriBuilder.build().toString();
    }


    /**
     * Parse JSON string into it's items
     *
     * @param items
     * @param jsonBody
     * @throws IOException
     * @throws JSONException
     */
    private void parseItems(List<GalleryItem>items, JSONObject jsonBody)
        throws IOException, JSONException
    {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray  photoJsonArray   = photosJsonObject.getJSONArray("photo");

        for(int i = 0; i < photoJsonArray.length(); i++)
        {
            JSONObject  photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item            = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s"))
            {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }   // end private void parseItems(List<GalleryItem>items, JSONObject jsonBody)
}   // end public class FlickrFetchr


