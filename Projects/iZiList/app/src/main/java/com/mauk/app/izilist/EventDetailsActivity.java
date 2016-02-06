package com.mauk.app.izilist;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Mauk on 01/11/2015.
 */
public class EventDetailsActivity extends BaseActivity {

    private static final String TAG = EventDetailsActivity.class.getSimpleName();
    private TextView title;
    private TextView hostname;
    private Button checkBtn;
    private SingleEvent event;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        activateToolbarWithHomeEnabled();

        event = (SingleEvent) getIntent().getSerializableExtra("SINGLE_EVENT_OBJECT");

        title = (TextView) findViewById(R.id.title);
        hostname = (TextView) findViewById(R.id.hostname);
        checkBtn = (Button) findViewById(R.id.check_button);

        title.setText(event.getTitle());
        hostname.setText(event.getHostname());
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //TODO: extract strings
                currentUser = ParseUser.getCurrentUser();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.getInBackground(event.getId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject parseEvent, ParseException e) {
                        if (e == null) {
                            ParseRelation<ParseObject> eventRelation = parseEvent.getRelation("guests");

                            try {
                                List<ParseObject> guests = eventRelation.getQuery().find();
                                if (guests.contains(currentUser)) {
                                    checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorAccent));
                                    title.setText(parseEvent.getObjectId());
                                } else {
                                    eventRelation.add(currentUser);
                                    parseEvent.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorAccent));
                                                title.setText(parseEvent.getObjectId());
                                            }
                                        }
                                    });
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }


}
