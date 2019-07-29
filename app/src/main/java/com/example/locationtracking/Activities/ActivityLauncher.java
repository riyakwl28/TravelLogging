package com.example.locationtracking.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.locationtracking.R;

public class ActivityLauncher extends AppCompatActivity {
    private int flag;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        flag=sharedPreferences.getInt("flag",0);
        id=sharedPreferences.getString("tripId"," ");

        Class<?> activityClass;
        try {
            SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
            activityClass = Class.forName(
                    prefs.getString("lastActivity", MainActivity.class.getName()));
        } catch(ClassNotFoundException ex) {
            activityClass = MainActivity.class;
        }
        if(flag==1)
        {
            Intent k=new Intent(ActivityLauncher.this, activityClass);
            k.putExtra("Trip Id",id);
            startActivity(k);
        }
        if(flag==0)
        {
            Intent i=new Intent(ActivityLauncher.this,activityClass);
            startActivity(i);
        }
        finish();


    }
}
