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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.locationtracking.R;
import com.example.locationtracking.Services.TrackerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button locationButton;
    ListView listView;
    String m_Text;
    private long startTime;
    private long endTime;

    String androidId;
    ArrayAdapter<String> adapter;
    List<String> ar;

    private static final int PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationButton=findViewById(R.id.track_button);

        listView=findViewById(R.id.track_list);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("id",androidId);
        ar = new ArrayList<>();


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child(androidId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uid = ds.getKey();
                    Log.e("id",uid);
                    ar.add(uid);

                }
                adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,ar);
                listView.setAdapter(adapter);
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
               String text = (String) listView.getItemAtPosition(position);
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
//                    String id = UUID.randomUUID().toString();
//                    String uniqueId = id.substring(0, 7);
                if(buttonText.equals("Enable Tracking")) {
                    startTime = System.currentTimeMillis();
                    b.setText("Disable Tracking");
                    buildDialog();
                }



                if(buttonText.equals("Disable Tracking"))
                {

                    endTime=getRunningTimeMillis();

                    Log.e("time",String.valueOf(endTime));

                    Intent i=new Intent(MainActivity.this,TrackerService.class);


                    stopService(i);
                    b.setText("Enable Tracking");


                }


            }
        });
    }

    private void buildDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Custom dialog");
        dialogBuilder.setMessage("Enter Trip Name");
        dialogBuilder.setPositiveButton("Start Tracking", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               m_Text=edt.getText().toString();
                startTracking();
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


    private void startTracking()
    {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }
    private void startTrackerService() {
        Intent i=new Intent(this,TrackerService.class);

//        i.putExtra("track id", id);
        i.putExtra("track text", m_Text);
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
