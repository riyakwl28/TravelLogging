package com.example.locationtracking.Activities;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.locationtracking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationDescActivity extends AppCompatActivity {
    private EditText editText;
    private Button descButton;
    private String m_text,androidId,tripId,locationId,s_text;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_desc);
        editText=findViewById(R.id.edit_details);
        descButton=findViewById(R.id.save_details);
        androidId=getIntent().getStringExtra("andId");
        tripId=getIntent().getStringExtra("tripId");
        locationId=getIntent().getStringExtra("locationId");
        databaseReference= FirebaseDatabase.getInstance().getReference().child(androidId).child(tripId).child("locations").child(locationId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("desc")){
                    editText.setText(dataSnapshot.child("desc").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_text=editText.getText().toString();
                if(m_text.isEmpty()){
                    m_text="N/A";
                }

                databaseReference= FirebaseDatabase.getInstance().getReference().child(androidId).child(tripId).child("locations").child(locationId).child("desc");
                databaseReference.setValue(m_text);
            }
        });

    }
}
