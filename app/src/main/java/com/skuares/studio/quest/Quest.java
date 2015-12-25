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

        Parse.initialize(this, "mCQlAqYREXDqNqLdbjB6C3d0lUzyaOxrK1E0PoUT", "1t5WDSGejFRKOUfPskGcQyugAMzNW9tZDBwl4tmG");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
