package com.example.locationtracking.Helpers;

import android.annotation.TargetApi;
import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import com.example.locationtracking.Activities.MainActivity;
import com.example.locationtracking.R;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager mManager;

    private static final String channelId="channel_1";
    private static final String channelName="my_channel";

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            createChannels();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels()
    {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorPrimary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager(){

        if(mManager==null)
        {

            mManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public Notification.Builder getChannelNotification(){

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle("Location Tracking")
                .setContentText("Location Tracking is Running")
                .setSmallIcon(R.drawable.ic_tracker)
                .setAutoCancel(true).setContentIntent(contentIntent);
    }
}
