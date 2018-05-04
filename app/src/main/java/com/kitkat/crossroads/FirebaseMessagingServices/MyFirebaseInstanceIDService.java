package com.kitkat.crossroads.FirebaseMessagingServices;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kitkat.crossroads.EnumClasses.DatabaseEntryNames;
import com.kitkat.crossroads.ExternalClasses.DatabaseConnections;

import android.util.Log;

/**
 * Every time the user logs into their account a new notification token is created.
 * This token then allows notifications to be sent to the users device.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    private final String TAG = "CrossRoadsMainMessenger";
    private DatabaseReference databaseReferenceUsersTable;
    private FirebaseAuth auth;

    /**
     * Gets the token that is registered to the device, this is called when ever the token is updated and whenever the user logs in.
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
     * Sends the current token to the server so it is stored and can be accessed.
     * @param refreshedToken The token registered to the current device.
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
