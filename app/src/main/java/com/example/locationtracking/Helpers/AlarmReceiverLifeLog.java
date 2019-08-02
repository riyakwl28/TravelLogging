package com.example.locationtracking.Helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.locationtracking.Services.TrackerService;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiverLifeLog extends BroadcastReceiver {

    private static final String TAG = "LL24";
    static Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(TAG, "Alarm for LifeLog...");
        SharedPreferences mPrefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        String str = mPrefs.getString("tripId", "");
        Long interval=Long.parseLong(mPrefs.getString("duration","1.0"));

        Intent i = new Intent(context, TrackerService.class);
        i.putExtra("fromWhere","alarm");
        i.putExtra("alarmId",str);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval,
                pi);

    }
}
