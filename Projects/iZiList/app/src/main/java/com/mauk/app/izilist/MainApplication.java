package com.mauk.app.izilist;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

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
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstalation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        //installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
