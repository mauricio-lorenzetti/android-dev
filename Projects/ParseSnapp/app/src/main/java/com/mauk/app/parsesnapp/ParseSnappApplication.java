package com.mauk.app.parsesnapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Created by Mauk on 25/10/2015.
 */
public class ParseSnappApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        FacebookSdk.sdkInitialize(this);
        Parse.initialize(this, "peXrHbwznK23U2pESdnt2G4tDbF9OoFPP06MSlEP", "q5by2OX5cQI6mFPkwJ55kfulMtDb4kKzf6xpXRYc");
        ParseUser.enableRevocableSessionInBackground();
        ParseFacebookUtils.initialize(this);
    }
}
