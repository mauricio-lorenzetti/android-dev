package com.mauk.app.izilist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Mauk on 31/10/2015.
 */
public class SignUpActivity extends BaseActivity {

    protected EditText usernameTxt;
    protected EditText passwordTxt;
    protected EditText emailTxt;
    protected Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameTxt = (EditText) findViewById(R.id.username_text);
        passwordTxt = (EditText) findViewById(R.id.password_text);
        emailTxt = (EditText) findViewById(R.id.email_text);
        signUpBtn = (Button) findViewById(R.id.sign_up_button);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameTxt.getText().toString().trim();
                String password = passwordTxt.getText().toString().trim();
                String email = emailTxt.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    //TODO: error empty fields
                } else {
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                //TODO: error signing up
                            }
                        }
                    });
                }
            }
        });

    }
}
