package com.example.locationtracking;

import android.graphics.Color;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapMarkerActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private String androidId;
    String id;
    List<LatLng> latLngs;

    DatabaseReference databaseReference;
    final String TAG = "PathGoogleMapActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_marker);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);



        id= getIntent().getStringExtra("Trip Id");
        Log.e("Map",id);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);






    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        databaseReference= FirebaseDatabase.getInstance().getReference().child(androidId).child(id).child("locations");
        ValueEventListener valueEventListener= databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count=1;
                latLngs=new ArrayList<>();
                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                    //get lat and long from database and store it in a list
                    Double lat = zoneSnapshot.child("latitude").getValue(Double.class);

                    Double lng = zoneSnapshot.child("longitude").getValue(Double.class);
                    LatLng point=new LatLng(lat,lng);

                    MarkerOptions marker = new MarkerOptions();
                    marker.position(point).title("Point"+count);

                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    mMap.addMarker(marker);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            point, 12));
// setting polyline in the map
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(5);
                    latLngs.add(point);
                    polylineOptions.addAll(latLngs);
                    mMap.addPolyline(polylineOptions);
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }

        });
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
