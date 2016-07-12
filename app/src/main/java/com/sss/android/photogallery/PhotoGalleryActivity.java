package com.sss.android.photogallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class PhotoGalleryActivity extends SingleFragmentActivity
{

    @Override
    public Fragment createFragment()
    {
        return PhotoGalleryFragment.newInstance();
    }
}
