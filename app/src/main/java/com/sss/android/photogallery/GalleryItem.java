package com.sss.android.photogallery;

/**
 * Created by Paul Shepherd on 7/11/2016.
 */
public class GalleryItem
{
    private String mCaption;
    private String mId;
    private String mUrl;

    @Override
    public String toString()
    {
        return mCaption;
    }

    /**
     * Getters and Setters for member variables
     */
    public String getCaption()               {return mCaption;}
    public void   setCaption(String caption) {mCaption = caption;}
    public String getId()                    {return mId;}
    public void   setId(String id)           {mId = id;}
    public String getUrl()                   {return mUrl;}
    public void   setUrl(String url)         {mUrl = url;}
}   // end public class GalleryItem
