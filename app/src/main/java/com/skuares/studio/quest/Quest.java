package com.skuares.studio.quest;

import android.app.Application;

import com.firebase.client.Firebase;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by salim on 12/14/2015.
 */
public class Quest extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        Parse.initialize(this, "tKsGTKmxQZkMkLsAJAfsJvjsL5dRYqLQv37th75U", "dDSwrYUK74aZd3TIHXExQ0SCBDYiXiwarVlut29m");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
