package com.mauk.app.parsesnapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends ListActivity {

    private static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> friendsRelation;
    protected ParseUser currentUser;
    protected List<ParseUser> users;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate delegate = AppCompatDelegate.create(this, null);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_edit_friends);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);

        currentUser = ParseUser.getCurrentUser();
        friendsRelation = currentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    // Success
                    users = list;
                    String[] usernames = new String[users.size()];
                    int i = 0;
                    for (ParseUser user : users) {
                        usernames[i++] = user.getUsername();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    setListAdapter(adapter);
                    addFriendChekmarks();
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void addFriendChekmarks() {
        friendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < users.size(); i++) {
                        ParseUser user = users.get(i);
                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {
            friendsRelation.add(users.get(position));
        } else {
            friendsRelation.remove(users.get(position));
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

}
