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
import com.google.firebase.database.ServerValue;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.Manifest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String id = null;
    String androidId;
    String trackId;
    Integer duration;
    private Handler handler;
    LocationRequest request;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            long cellID = 0;
            String locationName=null;

            int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    GsmCellLocation loc = (GsmCellLocation) telephony.getCellLocation();
                    if (loc != null) {
                        cellID = loc.getCid() & 0xffff;
                    }
                }
            }
            Log.e("outside", String.valueOf(cellID));



                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(androidId).child(trackId).child("locations");
                Location location = locationResult.getLastLocation();
                Double lat = locationResult.getLastLocation().getLatitude();

                Double lng = locationResult.getLastLocation().getLongitude();
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();








                if (location != null ) {
                    locationName = getAddress(lat, lng);
                    if(locationName==null){
                        locationName="N/A";
                    }
                    LocationDetails locationDetails = new LocationDetails(lat, lng, cellID, locationName, ts);
                    Log.e(TAG, "location update " + location);
                    ref.push().setValue(locationDetails);
                }

        }

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






    }

    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        String strdata = intent.getStringExtra("fromWhere");
        if(strdata.equals("main")){
            trackId=intent.getStringExtra("track id");
        }
        else if(strdata.equals("boot")){
            trackId=intent.getStringExtra("broadcastId");
        }
        duration=Integer.valueOf(intent.getStringExtra("duration"));

        Log.e("Track id",trackId);

        new CheckInternetAsyncTask(getApplicationContext()).execute();


        return START_STICKY;

    }




    private void requestLocationUpdates() {
        request = new LocationRequest();
        int Interval=10*60*1000;
        request.setInterval(Interval);
        request.setFastestInterval(duration*60*1000);
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



            Log.v("IGA", "Address" + add);

        } catch (IOException e) {

            e.printStackTrace();
            Log.e("Getting address","failed");
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

private class CheckInternetAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    private Context context;

    public CheckInternetAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();


        if (isConnected) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0)
                    return true;

            } catch (IOException e) {
                Log.e("TAG", "Error checking internet connection", e);
                return false;
            }
        } else {
            Log.d("TAG", "No network available!");
            return false;
        }


        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        Log.d("TAG", "result" + result);

        if(result){
            startForeground(123456789, getNotification());
            Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();
            requestLocationUpdates();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"NO Network Available",Toast.LENGTH_LONG).show();
        }

    }


}

}

