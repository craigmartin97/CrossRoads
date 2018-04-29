package com.kitkat.crossroads.MainActivity;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Every time the app is ran this class is called to enable data persistence while the users
 * device is offline. This will ensure that data is cached locally to the phone for a better user
 * experience.
 */
public class CrossRoads extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
