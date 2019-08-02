package com.example.locationtracking.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.locationtracking.Models.LocationAdapter;
import com.example.locationtracking.Models.LocationNameData;
import com.example.locationtracking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {


    private String tripId;

    private ListView listView;
    private List<LocationNameData> locationNameList;
    private LocationAdapter locationAdapter;
    private DatabaseReference databaseReference;
    private String androidId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tripId = getIntent().getStringExtra("tripId");
        listView = findViewById(R.id.location_lv);


        if (tripId != null) {

            androidId = getIntent().getStringExtra("androidId");
            databaseReference = FirebaseDatabase.getInstance().getReference().child(androidId).child(tripId).child("locations");
            Query chatQuery = databaseReference.orderByChild("timeStamp").limitToLast(100);
            chatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    int count = 0;
                    String lac, mcc, mnc, cellId, mode;
                    String name, lastD, time;
                    locationNameList = new ArrayList<>();
                    for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                        String id = zoneSnapshot.getKey();

                        name = zoneSnapshot.child("locationName").getValue().toString();
                        if (zoneSnapshot.child("distance").exists()) {
                            lastD = zoneSnapshot.child("distance").getValue().toString();
                        } else {
                            lastD = "N/A";
                        }
                        if (zoneSnapshot.child("start").exists()) {
                            time = zoneSnapshot.child("start").getValue().toString();
                        } else {
                            time = "N/A";
                        }

                        if (zoneSnapshot.child("cellId").exists()) {
                            cellId = zoneSnapshot.child("cellId").getValue().toString();
                        } else {
                            cellId = "N/A";
                        }
                        if (zoneSnapshot.child("lac").exists()) {
                            lac = zoneSnapshot.child("lac").getValue().toString();
                        } else {
                            lac = "N/A";
                        }
                        if (zoneSnapshot.child("mcc").exists()) {
                            mcc = zoneSnapshot.child("mcc").getValue().toString();
                        } else {
                            mcc = "N/A";
                        }
                        if (zoneSnapshot.child("mnc").exists()) {
                            mnc = zoneSnapshot.child("mnc").getValue().toString();
                        } else {
                            mnc = "N/A";
                        }
                        if (zoneSnapshot.child("mode").exists()) {
                            mode = zoneSnapshot.child("mode").getValue().toString();
                        } else {
                            mode = "N/A";
                        }


                        int number = count + 1;
                        LocationNameData locationNameData = new LocationNameData(name, number, id, androidId, tripId, lastD, time, cellId, lac, mcc, mnc, mode);
                        locationNameList.add(locationNameData);
                        count++;
                    }
                    locationAdapter = new LocationAdapter(LocationActivity.this, R.layout.list_location_item, locationNameList);
                    listView.setStackFromBottom(false);
                    listView.setAdapter(locationAdapter);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.show_map, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_map) {
            Intent intent = new Intent(getApplicationContext(), MapMarkerActivity.class);
            intent.putExtra("androidId",androidId);
            intent.putExtra("Trip Id",tripId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
