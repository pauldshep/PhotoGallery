package com.sss.android.photogallery;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Class SingleFragmentActivity.  Extends AppCompatActivity instead of
 * FragmentActivity in order to get a tool bar.
 */
//public abstract class SingleFragmentActivity extends FragmentActivity
public abstract class SingleFragmentActivity extends AppCompatActivity
{
    protected abstract Fragment createFragment();

    protected int getLayoutResId()
    {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}   // end public abstract class SingleFragmentActivity extends AppCompatActivity
