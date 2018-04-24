package com.kitkat.crossroads.ExternalClasses;

import com.google.firebase.database.DataSnapshot;

/**
 * Holds methods to iterate through FireBase database tables
 */
public class DatabaseReferences
{
    /**
     * Get the data from the FireBase table
     *
     * @param dataSnapshot - Snapshot of the FireBase database
     * @return - contents of the FireBase database table
     */
    public DataSnapshot getTableReference(DataSnapshot dataSnapshot, String table)
    {
        return dataSnapshot.child(table);
    }

    /**
     * Get the children from the FireBase table
     *
     * @return jobReference - Snapshot of the FireBase database table
     */
    public Iterable<DataSnapshot> getTableChildren(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getChildren();
    }
}
