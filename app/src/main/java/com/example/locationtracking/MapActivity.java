package com.example.locationtracking;

import android.app.Activity;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.AsyncTask;


import com.google.android.gms.maps.model.MarkerOptions;



public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng TIMES_SQUARE = new LatLng(40.7577, -73.9857);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);

    private GoogleMap mMap;
    private String androidId;
    static List<Double> latitudes;
    static List<Double> longitudes;
    DatabaseReference databaseReference;
    final String TAG = "PathGoogleMapActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

       String id= getIntent().getStringExtra("Trip Id");
        Log.e("Map",id);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        latitudes = new ArrayList<>();
        longitudes=new ArrayList<>();


     databaseReference= FirebaseDatabase.getInstance().getReference().child(androidId).child(id).child("locations");

     databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                                                            //get lat and long from database and store it in a list
                                                            Double lat = zoneSnapshot.child("latitude").getValue(Double.class);
                                                            latitudes.add(lat);
                                                            Double lng = zoneSnapshot.child("longitude").getValue(Double.class);
                                                            longitudes.add(lng);
                                                            Log.i("TAG", latitudes.toString());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
     Log.e("tag",latitudes.toString());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);






    }

    private String getMapsApiDirectionsUrl() {
//        String waypoints = "waypoints=optimize:true|"
//                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
//                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
//                + BROOKLYN_BRIDGE.longitude + "|" + TIMES_SQUARE.latitude + ","
//                + TIMES_SQUARE.longitude;


        //String params = waypoints + "&" + sensor;
        //String output = "json";
        //String url = "https://maps.googleapis.com/maps/api/directions/"
//                + output + "?" + params;
//        return url;




        // Origin of route
        String str_origin = "origin="+LOWER_MANHATTAN.latitude+","+LOWER_MANHATTAN.longitude;

        // Destination of route
        String str_dest = "destination="+TIMES_SQUARE.latitude+","+TIMES_SQUARE.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

            String waypoints="waypoints="+"via:-"+BROOKLYN_BRIDGE.latitude+"%2C"+BROOKLYN_BRIDGE.longitude;
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;

    }



    private void addMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            mMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            mMap.addMarker(new MarkerOptions().position(TIMES_SQUARE)
                    .title("Third Point"));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points=null ;
            PolylineOptions polyLineOptions = new PolylineOptions();

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }








//    private void addLines() {
//
//        mMap.addPolyline((new PolylineOptions())
//                        .add(TIMES_SQUARE, BROOKLYN_BRIDGE, LOWER_MANHATTAN,
//                                TIMES_SQUARE).width(5).color(Color.BLUE)
//                        .geodesic(true));
//        // move camera to zoom on map
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOWER_MANHATTAN,
//                13));
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap=googleMap;
        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(TIMES_SQUARE);


        mMap.addMarker(options);

        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                13));
        addMarkers();


    }
}
