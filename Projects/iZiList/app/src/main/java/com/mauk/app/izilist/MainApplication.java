package com.mauk.app.izilist;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Created by Mauk on 30/10/2015.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(this);
        Parse.initialize(this, "cmZsVtdiiRpFiiBVN3pZbaLSzuVRgdKcOrwMDuBy", "zSRzniNepM37xqVyHBIIMYVMlJRlTynUFkK7Ldpu");
        ParseUser.enableRevocableSessionInBackground();
        ParseFacebookUtils.initialize(this);

    }
}
