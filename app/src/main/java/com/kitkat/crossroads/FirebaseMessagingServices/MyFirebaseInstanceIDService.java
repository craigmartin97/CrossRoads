package com.kitkat.crossroads.FirebaseMessagingServices;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kitkat.crossroads.EnumClasses.DatabaseEntryNames;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;

import android.util.Log;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    private final String TAG = "CrossRoadsMainMessenger";
    private DatabaseReference databaseReferenceUsersTable;
    private FirebaseAuth auth;

    /**
     *
     */
    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * @param refreshedToken
     */
    private void sendRegistrationToServer(String refreshedToken)
    {
        DatabaseConnections databaseConnections = new DatabaseConnections();
        databaseReferenceUsersTable = databaseConnections.getDatabaseReferenceUsers();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null)
        {
            databaseReferenceUsersTable = FirebaseDatabase.getInstance().getReference();
            databaseReferenceUsersTable.child(auth.getCurrentUser().getUid()).child(DatabaseEntryNames.notifToken.name()).setValue(refreshedToken);
        }
    }


}
