package com.mauk.app.izilist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int UPDATE_EVENT = 0;

    private TextView emptyView;
    private RecyclerView recyclerView;
    private EventRecyclerViewAdapter eventAdapter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<SingleEvent> events = new ArrayList<SingleEvent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar();

        emptyView = (TextView) findViewById(R.id.empty_view);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEvents();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        verifyCurrentSession();

        eventAdapter = new EventRecyclerViewAdapter(this, events);
        recyclerView.setAdapter(eventAdapter);

        if (emptyView.getVisibility() == View.VISIBLE) {
            updateEvents();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult called " + " " + data.getIntExtra("EVENT_OBJECT_POSITION", -1) + " " + ((SingleEvent) data.getSerializableExtra("SINGLE_EVENT_OBJECT")).getAmIguest());
        if ((requestCode == UPDATE_EVENT) && (resultCode == RESULT_OK)) {
            int position = data.getIntExtra("EVENT_OBJECT_POSITION", -1);
            SingleEvent event = (SingleEvent) data.getSerializableExtra("SINGLE_EVENT_OBJECT");
            Log.v(TAG, event.getId() + " " + events.get(position).getId());
            if (event.getId().equals(events.get(position).getId())) {
                events.get(position).setAmIguest(event.getAmIguest());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //Perform the final search
                        Toast.makeText(getBaseContext(), "finalizou!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange (String newText) {
                        // Text has change, apply filtering...
                        Toast.makeText(getBaseContext(), "mudou", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                ParseUser.logOut();
                this.recreate();
                break;
            case R.id.action_refresh:
                updateEvents();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyCurrentSession() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            Log.i(TAG, currentUser.getUsername());
            if (!currentUser.containsKey("fullName")) {
                requestFullName();
            }
        } else {
            navigateToLogin();
        }
    }

    private void requestFullName() {
        Intent intent = new Intent(MainActivity.this, FullNameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateEvents() {
        final ArrayList<String> userEvents = new ArrayList<String>();

        setProgressSpinnerVisibility(true);
        swipeContainer.setRefreshing(true);

        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Event");
        q.whereEqualTo("guests", ParseUser.getCurrentUser());
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null) {
                    for (ParseObject o : objects) {
                        userEvents.add(o.getObjectId());
                        Log.v(TAG, o.getObjectId());
                    }
                }
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
                query.addDescendingOrder("createdAt");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (objects != null) {
                            if (objects.size() > 0) {
                                events.clear();
                                for (int i = 0; i < objects.size(); i++) {
                                    SingleEvent event =
                                            new SingleEvent(
                                                    objects.get(i).getObjectId(),
                                                    objects.get(i).getString(ParseConstants.KEY_TITLE),
                                                    objects.get(i).getString(ParseConstants.KEY_HOSTNAME),
                                                    objects.get(i).getString(ParseConstants.KEY_ADDRESS),
                                                    objects.get(i).getDate(ParseConstants.KEY_DATE),
                                                    objects.get(i).getString(ParseConstants.KEY_DESCRIPTION),
                                                    objects.get(i).getString(ParseConstants.KEY_PRICE),
                                                    objects.get(i).getParseFile(ParseConstants.KEY_MAIN_IMG_URI) != null ?
                                                            Uri.parse(objects.get(i).getParseFile(ParseConstants.KEY_MAIN_IMG_URI).getUrl()).toString() : new String(),
                                                    objects.get(i).getParseFile(ParseConstants.KEY_LOGO_IMG_URI) != null ?
                                                            Uri.parse(objects.get(i).getParseFile(ParseConstants.KEY_LOGO_IMG_URI).getUrl()).toString() : new String(),
                                                    userEvents.contains(objects.get(i).getObjectId())
                                            );
                                    events.add(event);
                                    Log.v(TAG, event.toString());
                                }
                                emptyView.setVisibility(View.GONE);
                                swipeContainer.setRefreshing(false);
                                setProgressSpinnerVisibility(false);
                                eventAdapter.notifyDataSetChanged();
                            } else {
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //TODO: network error
                        }
                    }
                });
            }
        });
    }
}
