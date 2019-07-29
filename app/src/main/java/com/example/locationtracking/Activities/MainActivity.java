package com.example.locationtracking.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationtracking.Models.CustomAdapter;
import com.example.locationtracking.Models.DistanceData;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button locationButton;
    ListView listView;
    String m_Text;
    int count=0;
    private long startTime;
    private long endTime;
    private  String timeHours;
    private String uniqueId2;
    private String edit_time_text;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ProgressBar mProgess;
    SharedPreferences pref;
    DistanceData distanceData;
    private TextView androidIdtv;
    private String start,end;


    SwipeRefreshLayout mSwipeRefreshLayout;

    private String uniqueId;
    String uId,dist;


    String androidId;

    CustomAdapter customAdapter;
    List<LocationData> ar;

    private static final int PERMISSIONS_REQUEST = 1;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationViewListner();

        mDrawerLayout=findViewById(R.id.drawer_layout);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationButton=findViewById(R.id.track_button);
        androidIdtv=findViewById(R.id.android_id_tv);



        distanceData=new DistanceData();
        listView=findViewById(R.id.track_list);
        mProgess=findViewById(R.id.pb_loading_indicator);


        ar = new ArrayList<>();

          String buttonState = LoadButtonState();
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


            if(buttonState.equals("Start Tracking")){
                locationButton.setText("Stop Tracking");
                count=preferences.getInt("count",0);
                start=preferences.getString("start",null);
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
                        new CheckInternetAsyncTask(MainActivity.this).execute();
                        customAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(0);
                    }
                }, 2000);
            }
        });
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        //get unique id for a particular device
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("id",androidId);


        androidIdtv.setText(androidId);


        //check for permission of location
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        new CheckInternetAsyncTask(MainActivity.this).execute();

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button)view;

                String buttonText = b.getText().toString();

                if(buttonText.equals("Start Tracking")) {
                    count=0;
                    uId=getUniqueId();
                    start= DateFormat.getDateTimeInstance().format(new Date());
                    IdDetail idDetail=new IdDetail(uId);
                    uniqueId2=idDetail.getUniqueId();
                    startTime = System.currentTimeMillis();
                    b.setText("Stop Tracking");
                    startTracking(uId);
                    SaveButtonState("Start Tracking");
                    editor.putString("start",start);
                    editor.putString("tripId",uniqueId2);
                    editor.putLong("starttime",startTime);
                    editor.putInt("count",count);
                    editor.putInt("flag",1);

                    editor.apply();
                    Intent i=new Intent(MainActivity.this,CurrentMapActivity.class);
                    i.putExtra("Trip Id",uniqueId2);
                    startActivity(i);
                }

                if(buttonText.equals("Stop Tracking"))
                {
                    SaveButtonState("Stop Tracking");
                    end=DateFormat.getDateTimeInstance().format(new Date());
                    endTime=getRunningTimeMillis();
                    timeHours= convertToMin(endTime);

                    editor.putInt("flag",0);
                    editor.apply();
                    Log.e("time",timeHours);
                    Log.e("stop", uniqueId2);
                    Intent i=new Intent(MainActivity.this,TrackerService.class);

                    stopService(i);





                    b.setText("Start Tracking");
                    getDistanceValues(uniqueId2);

                        buildDialog(uniqueId2);




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
        new CheckInternetAsyncTask(MainActivity.this).execute();
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
                    String startTime;
                    String endTime;
                    String distance;
                    Log.e("id", uid);

                    if(ds.child("totalDist").exists()) {
                        distance = ds.child("totalDist").getValue().toString();
                    }
                    else{
                        distance="N/A";
                    }
                    if(ds.child("others").exists()) {
                        String trackName = ds.child("others").child("trackName").getValue().toString();

                        String time = ds.child("others").child("time").getValue().toString();
                        if(ds.child("others").child("startTime").exists()) {
                            startTime = ds.child("others").child("startTime").getValue().toString();
                        }
                        else{
                            startTime=String.valueOf(0);
                        }
                        if(ds.child("others").child("endTime").exists()) {
                            endTime = ds.child("others").child("endTime").getValue().toString();
                        }
                        else {
                            endTime=String.valueOf(0);
                        }
                        LocationData locationData = new LocationData(uid, trackName, distance, time,startTime,endTime,androidId);
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




    private String convertToMin(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;

    }


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

                LocationOthersDetails locationOthersDetails=new LocationOthersDetails(m_Text,timeHours,start,end);
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




    private void getDistanceValues(final String id){
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(androidId).child(id).child("locations");
        Query chatQuery = ref.orderByChild("timeStamp"). limitToLast(100);
        chatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double dist=0.00;

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Double distance=Double.valueOf(ds.child("distance").getValue().toString());
                    dist=distance+dist;
                }
                DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child(androidId).child(id).child("totalDist");
                ref1.setValue(String.valueOf(dist));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences preferences=getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor edit=preferences.edit();
       if( mToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();
       double durationMenu;
        switch (id){
            case R.id.item4:
                durationMenu=0.5;
                Toast.makeText(getApplicationContext(),"Tracking for every 30 seconds",Toast.LENGTH_LONG).show();
                edit.putString("duration",String.valueOf(durationMenu));
                edit.apply();
                return true;
            case R.id.item0:
                durationMenu=1;
                Toast.makeText(getApplicationContext(),"Tracking for every 1 minute",Toast.LENGTH_LONG).show();
                edit.putString("duration",String.valueOf(durationMenu));
                edit.apply();
                return true;
            case R.id.item1:
                durationMenu=2;
                Toast.makeText(getApplicationContext(),"Tracking for every 2 minutes",Toast.LENGTH_LONG).show();
                edit.putString("duration",String.valueOf(durationMenu));
                edit.apply();
                return true;
            case R.id.item2:
                durationMenu=5;
                Toast.makeText(getApplicationContext(),"Tracking for every 5 minutes",Toast.LENGTH_LONG).show();
                edit.putString("duration",String.valueOf(durationMenu));
                edit.apply();
                return true;
            case R.id.item3:
                durationMenu=10;
                Toast.makeText(getApplicationContext(),"Tracking for every 10 minutes",Toast.LENGTH_LONG).show();
                edit.putString("duration",String.valueOf(durationMenu));
                edit.apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.settings:
                buildDialogTime();
                return true;
            case R.id.about_us:
                Intent i=new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(i);
                return true;
            case R.id.working:
                Intent j=new Intent(MainActivity.this,HowItWorksActivity.class);
                startActivity(j);
                return true;
            case  R.id.current:
                Intent k=new Intent(MainActivity.this, CurrentMapActivity.class);
                k.putExtra("Trip Id",uniqueId2);
                startActivity(k);
                return true;


        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void buildDialogTime() {


        SharedPreferences preferences=getSharedPreferences("MyPref",MODE_PRIVATE);
        final SharedPreferences.Editor edit=preferences.edit();



        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_time, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit_time);

        dialogBuilder.setTitle("Location Update Time");
        dialogBuilder.setMessage("Enter in Minutes(Number)");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
             edit_time_text=edt.getText().toString();
             double durationMenu=Integer.valueOf(edit_time_text);
             edit.putString("duration",String.valueOf(durationMenu));
             edit.apply();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

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
              getListItems();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Error Downloading data",Toast.LENGTH_LONG).show();
            }

        }


    }


}
