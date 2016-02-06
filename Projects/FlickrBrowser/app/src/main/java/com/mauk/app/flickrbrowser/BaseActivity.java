package com.mauk.app.flickrbrowser;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Mauk on 21/10/2015.
 */
public class BaseActivity extends AppCompatActivity {

    public static final String PHOTO_TRANSFER = "PHOTO_TRANSFER";
    public static final String FLICKR_QUERY = "FLICKR_QUERY";

    private Toolbar toolbar;

    protected Toolbar activateToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.app_bar);
        }
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        return toolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (toolbar != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        return toolbar;

    }

}
