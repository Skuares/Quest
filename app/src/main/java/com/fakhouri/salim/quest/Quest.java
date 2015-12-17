package com.fakhouri.salim.quest;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by salim on 12/14/2015.
 */
public class Quest extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
