package com.kitkat.crossroads.FirebaseMessagingServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.barteksc.pdfviewer.util.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.SplashScreen;

/**
 * This class deals with the message received from a notification.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService implements Constants
{

    private static final String TAG = "MyFirebaseMsgService";
    private static int count = 0;

    /**
     * This method runs whenever a notification is received.
     * @param remoteMessage The message received from the notification.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "Notification Message TITLE: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "Notification Message BODY: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Notification Message DATA: " + remoteMessage.getData().get("id").toString());

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(), remoteMessage.getData().get("id").toString());
    }

    //This method is only generating push notification

    /**
     * This method styles the notification and then displays it to the users device.
     * @param messageTitle Notification Title
     * @param messageBody Notification Body
     * @param tag data from the notification
     */
    private void sendNotification(String messageTitle, String messageBody, String tag)
    {
        PendingIntent contentIntent = null;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.iconcrossroadscwhite))
                .setSmallIcon(R.drawable.iconcrossroadscwhite)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent);


//        Intent notificationIntent = null;
//
//        if (tag.equals("acceptBidNotification"))
//        {
//            notificationIntent = new Intent(MyFirebaseMessagingService.this, SplashScreen.class);
//            notificationIntent.putExtra("menuFragment", "myJobsFragment");
//            notificationIntent.putExtra("tabView", "Active");
//        } else if (tag.equals("newBidNotification"))
//        {
//            notificationIntent = new Intent(MyFirebaseMessagingService.this, SplashScreen.class);
//            notificationIntent.putExtra("menuFragment", "myAdvertsFragment");
//            notificationIntent.putExtra("tabView", "Pending");
//        } else if (tag.equals("jobCompletedNotification"))
//        {
//            notificationIntent = new Intent(MyFirebaseMessagingService.this, SplashScreen.class);
//            notificationIntent.putExtra("menuFragment", "myAdvertsFragment");
//            notificationIntent.putExtra("tabView", "Completed");
//        }
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(count, notificationBuilder.build());
        count++;
    }
}