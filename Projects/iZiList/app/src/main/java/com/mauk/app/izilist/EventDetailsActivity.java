package com.mauk.app.izilist;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mauk on 01/11/2015.
 */
public class EventDetailsActivity extends BaseActivity {

    private static final String TAG = EventDetailsActivity.class.getSimpleName();
    private ImageView mainImage;
    private TextView hostname;
    private TextView date;
    private TextView price;
    private Button checkBtn;
    private SingleEvent event;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        activateToolbarWithHomeEnabled();

        event = (SingleEvent) getIntent().getSerializableExtra("SINGLE_EVENT_OBJECT");

        mainImage = (ImageView) findViewById(R.id.image);
        if (event.getImageUri() != null) {
            Uri imageUri = Uri.parse(event.getImageUri());
            Picasso.with(this).load(imageUri).placeholder(R.drawable.chelsea).into(mainImage);
        }

        hostname = (TextView) findViewById(R.id.hostname);
        date= (TextView) findViewById(R.id.date);
        price = (TextView) findViewById(R.id.price);
        checkBtn = (Button) findViewById(R.id.check_button);

        SimpleDateFormat df = SimpleDateFormat.getDateInstance("dd/MM", Locale.getDefault());

        hostname.setText(event.getHostname());
        date.setText(event.getDate() != null ? df.format(event.getDate()) : null);
        price.setText(event.getPrice());
        if (event.getAmIguest()) {
            checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorAccent));
            checkBtn.setText("nome adicionado!!");
        }

        //TODO: CloudCode this
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.listLoading));
                checkBtn.setText("Aguarde um momento...");
                currentUser = ParseUser.getCurrentUser();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("guests", currentUser);
                query.whereEqualTo("objectId", event.getId());
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(int n, ParseException e) {
                        if (e == null) {
                            if (n <= 0) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                                query.getInBackground(event.getId(), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseEvent, ParseException e) {
                                        // n <= 0 : usuario não está nesse evento >> colocar nome na lista
                                        ParseRelation<ParseObject> eventRelation = parseEvent.getRelation("guests");
                                        eventRelation.add(currentUser);
                                        parseEvent.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorAccent));
                                                    checkBtn.setText(R.string.name_added_button_label);
                                                    Toast.makeText(getBaseContext(), R.string.name_added_message, Toast.LENGTH_SHORT).show();
                                                    event.setAmIguest(true);
                                                } else {
                                                    Toast.makeText(getBaseContext(), R.string.there_is_a_problem_message, Toast.LENGTH_LONG).show();
                                                    checkBtn.setText(R.string.in_list_button_label);
                                                    checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.listUnchecked));
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                                query.getInBackground(event.getId(), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseEvent, ParseException e) {
                                        // n <= 0 : usuario não está nesse evento >> colocar nome na lista
                                        ParseRelation<ParseObject> eventRelation = parseEvent.getRelation("guests");
                                        eventRelation.remove(currentUser);
                                        parseEvent.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Toast.makeText(getBaseContext(), R.string.name_removed_message, Toast.LENGTH_SHORT).show();
                                                    checkBtn.setText(R.string.in_list_button_label);
                                                    checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.listUnchecked));
                                                    event.setAmIguest(false);
                                                } else {
                                                    Toast.makeText(getBaseContext(), R.string.there_is_a_problem_message, Toast.LENGTH_LONG).show();
                                                    checkBtn.setText(R.string.in_list_button_label);
                                                    checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.listUnchecked));
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getBaseContext(), R.string.there_is_a_problem_message, Toast.LENGTH_LONG).show();
                            checkBtn.setText(R.string.in_list_button_label);
                            checkBtn.setBackgroundColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.listUnchecked));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("SINGLE_EVENT_OBJECT", event);
        intent.putExtra("EVENT_OBJECT_POSITION", getIntent().getSerializableExtra("EVENT_OBJECT_POSITION"));
        setResult(RESULT_OK, intent);
        finish();
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
