package com.sss.android.photogallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * Class PhotoGalleryActivity
 */
public class PhotoGalleryActivity extends SingleFragmentActivity
//public class PhotoGalleryActivity extends AppCompatActivity
{
    public Fragment createFragment()
    {
        return PhotoGalleryFragment.newInstance();
    }
}
