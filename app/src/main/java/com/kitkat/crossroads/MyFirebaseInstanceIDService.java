package com.kitkat.crossroads;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.util.Log;


/**
 * Created by q5063319 on 19/03/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private final String TAG = "CrossRoads Messenger";
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {

        auth = FirebaseAuth.getInstance();

        if (!auth.getCurrentUser().getUid().equals(null)) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("notifToken").setValue(refreshedToken);
        }
    }


}
