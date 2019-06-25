package com.example.locationtracking;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.Manifest;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String id=null;
    String androidId;
    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("ID",androidId);

        Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();

        requestLocationUpdates();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       id= intent.getStringExtra("track id");
       Log.e("Track id",id);
        return super.onStartCommand(intent, flags, startId);

    }




    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        int Interval=10000;
        request.setInterval(Interval);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
//                    final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id).child("locations");
//                    Location location = locationResult.getLastLocation();
//                    if (location != null) {
//                        Log.e(TAG, "location update " + location);
//                        ref.push().setValue(location);
//                    }
                   long cellID = 0;
                    int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                            GsmCellLocation loc = (GsmCellLocation) telephony.getCellLocation();
                            if (loc != null)
                            {
                                cellID = loc.getCid() & 0xffff;
                            }
                        }
                    }
                    Log.e("outside",String.valueOf(cellID));



                    final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id).child("locations");
                    Location location=locationResult.getLastLocation();
                    Double lat = locationResult.getLastLocation().getLatitude();

                    Double lng=locationResult.getLastLocation().getLongitude();
                    LocationDetails locationDetails = new LocationDetails(lat,lng,cellID);
                    if (location != null) {
                        Log.e(TAG, "location update " + location);
                       ref.push().setValue(locationDetails);
                    }


                }
            }, null);
        }
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Log.e("stop","activity stopped");
    }

}