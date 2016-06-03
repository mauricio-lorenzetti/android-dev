package com.mauk.app.parsesnapp.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mauk.app.parsesnapp.adapters.UserAdapter;
import com.mauk.app.parsesnapp.utils.ParseConstants;
import com.mauk.app.parsesnapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {

    private static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> friendsRelation;
    protected ParseUser currentUser;
    protected List<ParseUser> users;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    protected GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        gridView = (GridView)findViewById(R.id.friendsGrid);
        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        gridView.setEmptyView(emptyTextView);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridView.setOnItemClickListener(onItemClickListener);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
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
                    if (gridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, users);
                        gridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter)gridView.getAdapter()).refill(users);
                    }
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
                                gridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    protected AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImage = (ImageView) view.findViewById(R.id.checkImage);

            if (gridView.isItemChecked(position)) {
                friendsRelation.add(users.get(position));
                checkImage.setVisibility(View.VISIBLE);
            } else {
                friendsRelation.remove(users.get(position));
                checkImage.setVisibility(View.INVISIBLE);
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
    };

}
