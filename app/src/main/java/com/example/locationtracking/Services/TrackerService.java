package com.example.locationtracking.Services;

import com.example.locationtracking.Activities.MainActivity;
import com.example.locationtracking.Models.LocationDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.Manifest;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String id=null;
    String androidId;
    String trackId;
    private Handler handler;
    LocationRequest request;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
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



            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(trackId).child("locations");
            Location location=locationResult.getLastLocation();
            Double lat = locationResult.getLastLocation().getLatitude();

            Double lng=locationResult.getLastLocation().getLongitude();
            String locationName=getAddress(lat,lng);
            Log.e("tracker",locationName);
            LocationDetails locationDetails = new LocationDetails(lat,lng,cellID);
            if (location != null) {
                Log.e(TAG, "location update " + location);
                ref.push().setValue(locationDetails);
            }

        };

    };
    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("ID",androidId);
        client = LocationServices.getFusedLocationProviderClient(this);

        Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String strdata = intent.getStringExtra("fromWhere");
        if(strdata.equals("main")){
            trackId=intent.getStringExtra("track id");
        }
        else if(strdata.equals("boot")){
            trackId=intent.getStringExtra("broadcastId");
        }

       Log.e("Track id",trackId);
        requestLocationUpdates();
        return super.onStartCommand(intent, flags, startId);

    }




    private void requestLocationUpdates() {
        request = new LocationRequest();
        int Interval=10000;
        request.setInterval(Interval);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, mLocationCallback, null);
        }
        }


    public String  getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String add=null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getCountryName();



            Log.v("IGA", "Address" + add);

        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      return add;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

        if(client!=null) {
            client.removeLocationUpdates(mLocationCallback);
            client = null;
            request=null;
        }

        stopSelf();
        Log.e("stop","activity stopped");

        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

}