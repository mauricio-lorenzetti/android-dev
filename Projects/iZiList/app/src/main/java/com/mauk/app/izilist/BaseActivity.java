package com.mauk.app.izilist;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by Mauk on 30/10/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;

    protected Toolbar activateToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        setSupportActionBar(toolbar);
        return toolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (toolbar != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        return toolbar;
    }

    public void setProgressSpinnerVisibility(boolean visibility) {
        if (progressBar == null) {
            progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        }
        progressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

}
