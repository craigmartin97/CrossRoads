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
 * <p>
 * Database connections is used to create a single repository to gain access to the FireBase
 * Database, Authentication and Storage area as well as other methods to get information about the user.
 */
public class DatabaseConnections extends AppCompatActivity
{
    /**
     * Get the connection to the FireBase authentication area
     *
     * @return - FireBaseAuth connection
     */
    public FirebaseAuth getAuth()
    {
        return FirebaseAuth.getInstance();
    }

    /**
     * Get the connection to the FireBase Database area
     *
     * @return - FireBase Database connection
     */
    public DatabaseReference getDatabaseReference()
    {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Get the connection to the FireBase Storage area
     *
     * @return - FireBase Storage area
     */
    public StorageReference getStorageReference()
    {
        return FirebaseStorage.getInstance().getReference();
    }

    /**
     * Get the current users unique Id
     *
     * @return - Current users Id
     */
    public String getCurrentUser()
    {
        return getAuth().getCurrentUser().getUid();
    }
}
