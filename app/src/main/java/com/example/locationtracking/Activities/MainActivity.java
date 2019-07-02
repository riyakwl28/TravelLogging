package com.example.locationtracking.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

   private String uniqueId;
   String uId;


    String androidId;

    CustomAdapter customAdapter;
    List<LocationData> ar;

    private static final int PERMISSIONS_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationButton=findViewById(R.id.track_button);

        listView=findViewById(R.id.track_list);
        ar = new ArrayList<>();

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("id",androidId);
//        ar = new ArrayList<>();
//        adapter=new ArrayAdapter<LocationOthersDetails>(this,R.layout.list_item,R.id.label,ar);


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference usersRef = rootRef.child(androidId);
        usersRef.keepSynced(true);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String uid = ds.getKey();
                        Log.e("id", uid);
                        String trackName = ds.child("others").child("trackName").getValue().toString();
                        String distance = ds.child("others").child("distance").getValue().toString();
                        String time = ds.child("others").child("time").getValue().toString();
                        LocationData locationData = new LocationData(uid,trackName, distance, time);
                        ar.add(locationData);
                        Collections.reverse(ar);

                }
                customAdapter = new CustomAdapter(MainActivity.this,R.layout.list_item,ar);
                listView.setStackFromBottom(false);
                listView.setAdapter(customAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);
       // int count=idList.size();

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick (AdapterView<?> parent,
                                             View view,
                                             int position,
                                             long id){

               Intent intent = new Intent(getApplicationContext(), MapMarkerActivity.class);
//               TextView textView = (TextView) view.findViewById(R.id.label);
//               String text = textView.getText().toString();
               String text =  ((TextView) view.findViewById(R.id.tripid)).getText().toString();
               Log.e("textView",text);
               intent.putExtra("Trip Id",text);
               startActivity(intent);
           }
       });




        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button)view;



                String buttonText = b.getText().toString();

                uId=getUniqueId();
                IdDetail idDetail=new IdDetail(uId);
                uniqueId2=idDetail.getUniqueId();



                if(buttonText.equals("Start Tracking")) {
                    startTime = System.currentTimeMillis();
                    b.setText("Stop Tracking");
                    startTracking(uId);
                }



                if(buttonText.equals("Stop Tracking"))
                {

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
               if(m_Text=="")
               {
                   m_Text="trip";
               }
               Log.e("dialog",id1);
                final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id1).child("others");
                LocationOthersDetails locationOthersDetails=new LocationOthersDetails(m_Text,"10",timeHours);
                ref.setValue(locationOthersDetails);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
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


        i.putExtra("track id", id);
        startService(i);


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
