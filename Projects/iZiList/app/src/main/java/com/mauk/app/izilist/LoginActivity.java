package com.mauk.app.izilist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mauk on 31/10/2015.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    protected EditText usernameTxt;
    protected EditText passwordTxt;
    protected Button loginBtn;
    protected Button FBloginBtn;
    protected TextView signUpText;
    protected LogInCallback logInCallback = new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
            if (user != null) {
                if (user.isNew()) {
                    Log.v(TAG, "New User");
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                //TODO: login failed
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpText = (TextView) findViewById(R.id.sign_up_text);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        usernameTxt = (EditText) findViewById(R.id.username_text);
        passwordTxt = (EditText) findViewById(R.id.password_text);
        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameTxt.getText().toString().trim();
                String password = passwordTxt.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    //TODO: login error message: fill all the fields
                } else {
                    ParseUser.logInInBackground(username, password, logInCallback);
                }
            }
        });

        FBloginBtn = (Button) findViewById(R.id.facebook_login_button);
        final List<String> permissions = Arrays.asList("user_friends", "email", "public_profile");
        FBloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, logInCallback);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}
