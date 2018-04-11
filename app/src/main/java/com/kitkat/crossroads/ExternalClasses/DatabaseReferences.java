package com.kitkat.crossroads.ExternalClasses;

import com.google.firebase.database.DataSnapshot;

public class DatabaseReferences
{
    /**
     * Get the data from the Bids table
     *
     * @param dataSnapshot
     * @return
     */
    public DataSnapshot getTableReference(DataSnapshot dataSnapshot, String table)
    {
        return dataSnapshot.child(table);
    }

    /**
     * Get the children of the Jobs table
     *
     * @return jobReference
     */
    public Iterable<DataSnapshot> getTableChildren(DataSnapshot dataSnapshot)
    {
        return dataSnapshot.getChildren();
    }
}
