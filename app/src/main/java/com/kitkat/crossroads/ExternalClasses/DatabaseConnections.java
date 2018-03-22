package com.kitkat.crossroads.ExternalClasses;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by q5031372 on 22/03/18.
 */

public class DatabaseConnections extends AppCompatActivity
{
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference myRef;

    public FirebaseAuth getAuth()
    {
        auth = FirebaseAuth.getInstance();
        return auth;
    }

    public DatabaseReference getDatabaseReference()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public DatabaseReference getMyRef()
    {
        myRef = FirebaseDatabase.getInstance().getReference();
        return myRef;
    }
}
