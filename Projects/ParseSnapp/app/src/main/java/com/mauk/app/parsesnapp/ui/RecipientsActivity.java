package com.mauk.app.parsesnapp.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mauk.app.parsesnapp.adapters.UserAdapter;
import com.mauk.app.parsesnapp.utils.FileHelper;
import com.mauk.app.parsesnapp.utils.ParseConstants;
import com.mauk.app.parsesnapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> friendsRelation;
    protected ParseUser currentUser;
    protected List<ParseUser> friends;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    protected MenuItem sendMenuItem;
    protected Uri mediaUri;
    protected String fileType;
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
                    if (gridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, friends);
                        gridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter)gridView.getAdapter()).refill(friends);
                    }
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
        sendMenuItem.setVisible(false);
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
                    sendPushNotifications();
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

    private void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

        //send push notifications
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message, ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
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
        for (int i = 0; i < gridView.getCount(); i++) {
            if (gridView.isItemChecked(i)){
                recipientIds.add(friends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (gridView.getCheckedItemCount() > 0) {
                sendMenuItem.setVisible(true);
            } else {
                sendMenuItem.setVisible(false);
            }

            ImageView checkImage = (ImageView) view.findViewById(R.id.checkImage);

            if (gridView.isItemChecked(position)) {
                checkImage.setVisibility(View.VISIBLE);
            } else {
                checkImage.setVisibility(View.INVISIBLE);
            }
        }
    };
}
