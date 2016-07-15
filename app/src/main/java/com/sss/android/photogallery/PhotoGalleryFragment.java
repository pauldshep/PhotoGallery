package com.sss.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Shepherd on 7/10/2016.
 */
public class PhotoGalleryFragment extends Fragment
{
    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView                     mPhotoRecyclerView;
    private List<GalleryItem>                mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;


    public static PhotoGalleryFragment newInstance()
    {
        return new PhotoGalleryFragment();
    }


    /**
     * Called when the fragment is created
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();

        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>()
                {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap)
                    {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }


    /**
     * Called when the view is destroyed
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }


    /**
     * Create and display the options menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        super.onCreateOptionsMenu(menu, menuInflater);
        Log.i(TAG, "onCreateOptionsMenu");

        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
           @Override
            public boolean onQueryTextSubmit(String s)
           {
               Log.d(TAG,"QueryTextSubmit: " + s);
               QueryPreferences.setStoredQuery(getActivity(), s);
               updateItems();
               return true;
           }

            @Override
            public boolean onQueryTextChange(String s)
            {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v)
           {
               String query = QueryPreferences.getStoredQuery(getActivity());
               searchView.setQuery(query, false);
           }
        });
    }   // end public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)


    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }   // end public boolean onOptionsItemSelected(MenuItem item)


    /**
     * Wrapper for the several places where you need to execute FetchItemsTask()
     */
    private void updateItems()
    {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    /**
     * Quit thumbnail downloader thread
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }


    private void setupAdapter()
    {
        if(isAdded())
        {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }


    /**
     * Internal Private Photo Holder class
     */
    private class PhotoHolder extends RecyclerView.ViewHolder
    {
        private ImageView mItemImageView;

        /**
         * Constructor
         *
         * @param itemView
         */
        public PhotoHolder(View itemView)
        {
            super(itemView);

            mItemImageView = (ImageView)itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }


        public void bindDrawable(Drawable drawable)
        {
            mItemImageView.setImageDrawable(drawable);
        }

    }   // end private class PhotoHolder extends RecyclerView.ViewHolder


    /**
     *
     */
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>
    {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List <GalleryItem>galleryItems)
        {
            mGalleryItems = galleryItems;
        }

        /**
         *
         * @param viewGroup
         * @param viewType
         * @return
         */
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);
        }


        /**
         *
         * @param photoHolder
         * @param position
         */
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position)
        {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable    placeholder = getResources().getDrawable(R.drawable.brian_up_close);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount()
        {
            return mGalleryItems.size();
        }

    }   // end private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>



    /**
     * Class FetchItemsTask: AsyncTask (short term on UI thread) that fetches
     * the Flickr photographs.
     */
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>
    {
        private String mQuery;

        public FetchItemsTask(String query)
        {
           mQuery = query;
        }


        @Override
        protected List<GalleryItem> doInBackground(Void... params)
        {

            if(mQuery == null)
            {
                return new FlickrFetchr().fetchRecentPhotos();
            }
            else
            {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items)
        {
            mItems = items;
            setupAdapter();
        }
    }   // end private class FetchItemsTask extends AsyncTask<Void, Void, Void>

}   // end public class PhotoGalleryFragment extends Fragment



