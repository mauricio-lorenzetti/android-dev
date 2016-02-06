package com.mauk.app.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private List<Photo> photoList = new ArrayList<Photo>();
    private RecyclerView recyclerView;
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(new ArrayList<Photo>(), MainActivity.this);
        recyclerView.setAdapter(flickrRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, position + "th item clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ViewPhotoDetailsActivity.class);
                intent.putExtra(PHOTO_TRANSFER, flickrRecyclerViewAdapter.getPhoto(position));
                startActivity(intent);
            }
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.getLayoutManager().scrollToPosition(0);
        String query = getSavedPreferenceData(FLICKR_QUERY);
        if (query.length() > 0) {
            ProcessPhotos processPhotos = new ProcessPhotos(query, true);
            processPhotos.execute();
        }
    }

    private String getSavedPreferenceData(String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getString(key, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.menu_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ProcessPhotos extends GetFlickrJSONData {

        public ProcessPhotos(String searchCriteria, boolean matchAll) {
            super(searchCriteria, matchAll);
        }

        @Override
        public void execute() {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJSONData {

            @Override
            protected void onPostExecute(String webData) {
                flickrRecyclerViewAdapter.loadNewData(getPhotoList());
                super.onPostExecute(webData);
            }

            @Override
            protected String doInBackground(String... params) {
                return super.doInBackground(params);
            }
        }
    }

}
