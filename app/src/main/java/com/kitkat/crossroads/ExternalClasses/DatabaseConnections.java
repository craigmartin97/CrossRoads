package com.kitkat.crossroads.ExternalClasses;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by q5031372 on 22/03/18.
 */

public class DatabaseConnections extends AppCompatActivity
{
    private FirebaseAuth auth;

    public FirebaseAuth getAuth()
    {
        return FirebaseAuth.getInstance();
    }

    public DatabaseReference getDatabaseReference()
    {
        return FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getMyRef()
    {
        return FirebaseDatabase.getInstance().getReference();
    }

    public StorageReference getStorageReference()
    {
        return FirebaseStorage.getInstance().getReference();
    }

    public String getCurrentUser()
    {
        return getAuth().getCurrentUser().getUid();
    }
}
