package com.mauk.app.izilist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        updateEvents();
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
                //TODO: extract string resources to static class
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
        setProgressSpinnerVisibility(true);
        swipeContainer.setRefreshing(true);

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
                                            objects.get(i).getString("title"),
                                            objects.get(i).getString("hostname"),
                                            "plchldr",
                                            new Date(),
                                            "plchldr",
                                            "plchldr");
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
}
