package com.example.locationtracking.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationtracking.Models.CustomAdapter;
import com.example.locationtracking.Models.IdDetail;
import com.example.locationtracking.Models.LocationData;
import com.example.locationtracking.Models.LocationOthersDetails;
import com.example.locationtracking.R;
import com.example.locationtracking.Services.TrackerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button locationButton;
    ListView listView;
    String m_Text;
    int count=0;
    private long startTime;
    private long endTime;
    private  String timeHours;
    private String uniqueId2;
    private MenuItem menuItemDelete;
    SharedPreferences pref;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private String uniqueId;
    String uId;


    String androidId;

    CustomAdapter customAdapter;
    List<LocationData> ar;

    private static final int PERMISSIONS_REQUEST = 1;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationButton=findViewById(R.id.track_button);

        listView=findViewById(R.id.track_list);

        ar = new ArrayList<>();

          String buttonState = LoadButtonState();
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


            if(buttonState.equals("Start Tracking")){
                locationButton.setText("Stop Tracking");
                count=preferences.getInt("count",0);
                startTime=preferences.getLong("starttime",0);
                uniqueId2=preferences.getString("tripId"," ");
                startTracking(preferences.getString("tripId",null));
            }
            else if(buttonState.equals("Stop Tracking")){
                locationButton.setText("Start Tracking");
            }





        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //handling swipe refresh
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(null);
                        ar=new ArrayList<>();
                        mSwipeRefreshLayout.setRefreshing(false);
                        getListItems();
                        customAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(0);
                    }
                }, 2000);
            }
        });

        //get unique id for a particular device
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("id",androidId);


        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        //check for permission of location
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        getListItems();



        //on click of item list go to map Activity belonging to a particular trip
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent,
                                     View view,
                                     int position,
                                     long id){

                Intent intent = new Intent(getApplicationContext(), MapMarkerActivity.class);
                String text =  ((TextView) view.findViewById(R.id.tripid)).getText().toString();
                Log.e("textView",text);
                intent.putExtra("Trip Id",text);
                startActivity(intent);
            }
        });

        //for deletion of list item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                customAdapter.handleLongPress(i, view);
                if(customAdapter.getListSelected().size() > 0){
                    showDeleteMenu(true);
                }else{
                    showDeleteMenu(false);
                }
                return true;
            }
        });



        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button)view;

                String buttonText = b.getText().toString();

                if(buttonText.equals("Start Tracking")) {
                    count=0;
                    uId=getUniqueId();
                    IdDetail idDetail=new IdDetail(uId);
                    uniqueId2=idDetail.getUniqueId();
                    startTime = System.currentTimeMillis();
                    b.setText("Stop Tracking");
                    startTracking(uId);
                    SaveButtonState("Start Tracking");
                    editor.putString("tripId",uniqueId2);
                    editor.putLong("starttime",startTime);
                    editor.putInt("count",count);
                    editor.apply();
                }

                if(buttonText.equals("Stop Tracking"))
                {
                    SaveButtonState("Stop Tracking");
                    endTime=getRunningTimeMillis();
                    timeHours= convertToMin(endTime);

                    Log.e("time",timeHours);
                    Log.e("stop", uniqueId2);

                    buildDialog(uniqueId2);

                    Intent i=new Intent(MainActivity.this,TrackerService.class);

                    stopService(i);

                    b.setText("Start Tracking");



                }


            }
        });

    }



    public void SaveButtonState(String bState){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("state", bState);
        edit.apply();
    }

    public String LoadButtonState(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        return preferences.getString("state", "DEFAULT");
    }

    private void retrieveValues() {
        listView.setAdapter(null);
        ar=new ArrayList<>();
        getListItems();
        customAdapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(0);
    }

    private void getListItems(){
        //get list of trips from database

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(androidId);
        rootRef.keepSynced(true);
        Query chatQuery = rootRef.orderByChild("timestamp"). limitToLast(100);
        chatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int count=0;

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uid = ds.getKey();
                    Log.e("id", uid);
                    if(ds.child("others").exists()) {
                        String trackName = ds.child("others").child("trackName").getValue().toString();
                        String distance = ds.child("others").child("distance").getValue().toString();
                        String time = ds.child("others").child("time").getValue().toString();
                        LocationData locationData = new LocationData(uid, trackName, distance, time);
                        ar.add(count,locationData);

                        count++;
                    }

                }
                Collections.reverse(ar);
                customAdapter = new CustomAdapter(MainActivity.this,R.layout.list_item,ar);
                listView.setStackFromBottom(false);
                listView.setAdapter(customAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    //to get trip id
    final private String getUniqueId()
    {

        if(count==0) {
            final String id = UUID.randomUUID().toString();
            final String uniqueId = id.substring(0, 7);
            count++;
            return uniqueId;

        }
        else{
            String uniqueId=uId;
            return uniqueId;
        }

    }


    private void showDeleteMenu(boolean show){
        menuItemDelete.setVisible(show);

    }


    private String convertToMin(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;

    }


//    private void diaologOnfinish() {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.dialog_onfinish, null);
//        dialogBuilder.setView(dialogView);
//
//        final TextView edt =  dialogView.findViewById(R.id.dialog_onfinish_tv);
//
//        dialogBuilder.setTitle("Tracking trip");
//        dialogBuilder.setMessage("Save trip");
//        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(m_Text).child("others");
//                LocationOthersDetails locationOthersDetails=new LocationOthersDetails(m_Text,"10",timeHours);
//                ref.setValue(locationOthersDetails);
//
//
//            }
//        });
//        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //pass
//            }
//        });
//        AlertDialog b = dialogBuilder.create();
//        b.show();
//
//
//
//    }

    private void buildDialog(String id) {
        final String id1=id;


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Tracking trip");
        dialogBuilder.setMessage("Enter Trip Name");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                m_Text=edt.getText().toString();
                if(m_Text.isEmpty())
                {
                    String seq=UUID.randomUUID().toString();
                    String sequence=seq.substring(0, 2);
                    m_Text="trip_"+sequence;
                }
                Log.e("dialog",id1);
                final DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child(androidId).child(id1).child("timestamp");
                ref2.setValue(ServerValue.TIMESTAMP);
                final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id1).child("others");
                LocationOthersDetails locationOthersDetails=new LocationOthersDetails(m_Text,"10",timeHours);
                ref.setValue(locationOthersDetails);
                retrieveValues();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id1);

                ref.removeValue();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    public long getRunningTimeMillis() {
        return System.currentTimeMillis() - startTime;
    }


    private void startTracking(String id)
    {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService(id);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }
    private void startTrackerService(String id) {
        Intent i=new Intent(this,TrackerService.class);
        i.putExtra("fromWhere","main");
        i.putExtra("track id", id);
        startService(i);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.delete_list, menu);

        menuItemDelete = menu.findItem(R.id.action_delete);

        menuItemDelete.setVisible(false);
        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                List<String > idList=customAdapter.getIdList();
                for(String id:idList){
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id);
                    ref.removeValue();
                }

                customAdapter.removeSelected();
                customAdapter.notifyDataSetChanged();
                showDeleteMenu(false);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
// super.onBackPressed();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
//            grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Start the service when the permission is granted
//            startTrackerService(id);
//        } else {
//            finish();
//        }
//    }
}
