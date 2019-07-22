package com.example.locationtracking.Activities;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        tripId= getIntent().getStringExtra("tripId");
        Log.e("LocationActivity",tripId);
        listView=findViewById(R.id.location_lv);


        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(androidId).child(tripId).child("locations");
        Query chatQuery = databaseReference.orderByChild("timeStamp"). limitToLast(100);
        chatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int count=0;
                String name;
                locationNameList=new ArrayList<>();
                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                    String id=zoneSnapshot.getKey();

                        name = zoneSnapshot.child("locationName").getValue().toString();

                    int number=count+1;
                    LocationNameData locationNameData=new LocationNameData(name,number,id,androidId,tripId);
                    locationNameList.add(locationNameData);
                    count++;
                }
               locationAdapter =new LocationAdapter(LocationActivity.this,R.layout.list_location_item,locationNameList);
                listView.setStackFromBottom(false);
                listView.setAdapter(locationAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
            intent.putExtra("Trip Id",tripId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
