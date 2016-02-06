package com.mauk.app.parsesnapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends ListActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> friendsRelation;
    protected ParseUser currentUser;
    protected List<ParseUser> friends;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    protected MenuItem sendMenuItem;
    protected Uri mediaUri;
    protected String fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate delegate = AppCompatDelegate.create(this, null);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_recipients);

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

        mediaUri = getIntent().getData();
        fileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
    }


    @Override
    protected void onResume() {
        super.onResume();

        currentUser = ParseUser.getCurrentUser();
        friendsRelation = currentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        progressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = friendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    friends = list;
                    String[] usernames = new String[friends.size()];
                    int i = 0;
                    for (ParseUser user : friends) {
                        usernames[i++] = user.getUsername();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        sendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                ParseObject message = createMessage();
                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RecipientsActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_uploading_file)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, fileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mediaUri);

        if (fileBytes == null) {
            return null;
        } else {
            if (fileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mediaUri, fileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<>();
        for (int i = 0; i < getListView().getCount(); i++) {
            if (getListView().isItemChecked(i)){
                recipientIds.add(friends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (l.getCheckedItemCount() > 0) {
            sendMenuItem.setVisible(true);
        } else {
            sendMenuItem.setVisible(false);
        }
    }
}
