package com.example.locationtracking.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.locationtracking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

public class LocationDescActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private EditText editText;
    private Button descButton;
    private ImageView mImageLabel;
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
        mImageLabel=findViewById(R.id.add_image);
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
                Toast.makeText(LocationDescActivity.this,"Data Saved",Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_add_image, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_add_image)
        {
            onLaunchCamera();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageLabel.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child(androidId).child(tripId).child("locations").child(locationId).child("imageUrl");
        ref.push().setValue(imageEncoded);
    }

}
