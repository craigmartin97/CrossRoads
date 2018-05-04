package com.kitkat.crossroads.ExternalClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kitkat.crossroads.EnumClasses.TableNames;

import java.util.Objects;

/**
 * Database connections is used to create a single repository to gain access to the FireBase
 * Database, Authentication and Storage area as well as other methods to get information about the user.
 */
public class DatabaseConnections
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
     * Get the connection to the FireBase database, under the Jobs table
     *
     * @return - FireBase Database Connection, Jobs table
     */
    public DatabaseReference getDatabaseReferenceJobs()
    {
        return FirebaseDatabase.getInstance().getReference().child(TableNames.Jobs.name());
    }

    /**
     * Get the connection to the FireBase database, under the Bids table
     *
     * @return - FireBase Database Connection, Bids table
     */
    public DatabaseReference getDatabaseReferenceBids()
    {
        return FirebaseDatabase.getInstance().getReference().child(TableNames.Bids.name());
    }

    /**
     * Get the connection to the FireBase database, under the Ratings table
     *
     * @return - FireBase Database Connection, Ratings table
     */
    public DatabaseReference getDatabaseReferenceRatings()
    {
        return FirebaseDatabase.getInstance().getReference().child(TableNames.Ratings.name());
    }

    /**
     * Get the connection to the FireBase database, under the Users table
     *
     * @return - FireBase Database Connection, Users table
     */
    public DatabaseReference getDatabaseReferenceUsers()
    {
        return FirebaseDatabase.getInstance().getReference().child(TableNames.Users.name());
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
        return Objects.requireNonNull(getAuth().getCurrentUser()).getUid();
    }
}
