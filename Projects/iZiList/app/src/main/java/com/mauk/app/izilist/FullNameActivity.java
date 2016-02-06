package com.mauk.app.izilist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Mauk on 31/10/2015.
 */
public class FullNameActivity extends BaseActivity {

    private static final String TAG = FullNameActivity.class.getSimpleName();

    protected EditText fullnameTxt;
    protected Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_name);

        fullnameTxt = (EditText) findViewById(R.id.fullname_text);
        submitBtn = (Button) findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullname = fullnameTxt.getText().toString().trim();

                if (fullname.isEmpty()) {
                    //TODO: error empty fields
                } else {
                    //TODO: extract string resources to static class
                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("fullName", fullname);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Saved!
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                ParseUser temp = new ParseUser();
                                try {
                                     temp = query.get(currentUser.getObjectId());
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                Log.v(TAG, fullname + " = " + currentUser.getString("fullName") + " = " + temp.getString("fullName"));
                                Intent intent = new Intent(FullNameActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                //Try again
                                AlertDialog.Builder builder = new AlertDialog.Builder(FullNameActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                fullnameTxt.getText().clear();
                                fullnameTxt.requestFocus();
                                //TODO: error message, network failure?
                            }
                        }
                    });
                }
            }
        });

    }

}
