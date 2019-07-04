package com.example.locationtracking.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.locationtracking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
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

                    int arrowColor = Color.RED; // change this if you want another color (Color.BLUE)
                    int lineColor = Color.RED;
                    BitmapDescriptor endCapIcon = getEndCapIcon(arrowColor);
// setting polyline in the map
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.color(Color.RED);
//                    polylineOptions.width(5);
//
//                    polylineOptions.addAll(latLngs);
                    latLngs.add(point);
                             mMap.addPolyline(new PolylineOptions()
                            .geodesic(true)
                            .color(lineColor)
                            .width(8)
                            .startCap(new RoundCap())
                            .endCap(new CustomCap(endCapIcon,8))
                            .jointType(JointType.ROUND)
                            .addAll(latLngs));

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

    public BitmapDescriptor getEndCapIcon( int color) {

        // mipmap icon - white arrow, pointing up, with point at center of image
        // you will want to create:  mdpi=24x24, hdpi=36x36, xhdpi=48x48, xxhdpi=72x72, xxxhdpi=96x96
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.arrow);

        // set the bounds to the whole image (may not be necessary ...)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        // overlay (multiply) your color over the white icon
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        // create a bitmap from the drawable
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // render the bitmap on a blank canvas
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        // create a BitmapDescriptor from the new bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
