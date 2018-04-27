package com.kitkat.crossroads.MainActivity;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by q5031372 on 24/04/18.
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
