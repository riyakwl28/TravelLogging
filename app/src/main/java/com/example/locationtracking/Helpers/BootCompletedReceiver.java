package com.example.locationtracking.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.locationtracking.Services.TrackerService;

import static android.content.Context.MODE_PRIVATE;

public class BootCompletedReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        // an Intent broadcast.
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            SharedPreferences mPrefs = context.getSharedPreferences("MyPref", MODE_PRIVATE);
            String str = mPrefs.getString("tripId", "");
            Intent i = new Intent(context, TrackerService.class);
            i.putExtra("fromWhere","boot");
            i.putExtra("broadcastId",str);
            context.startService(i);
        }
    }
}
