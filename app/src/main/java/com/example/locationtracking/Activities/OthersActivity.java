package com.example.locationtracking.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.locationtracking.R;

public class OthersActivity extends AppCompatActivity {

    private EditText userEd,tripEd;
    private Button submitBtn;
    private String tripId,userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        userEd=findViewById(R.id.user_edit_text);
        tripEd=findViewById(R.id.trip_edit_text);
        submitBtn=findViewById(R.id.submit_button);



        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripId=tripEd.getText().toString();
                userId=userEd.getText().toString();
                Intent i=new Intent(OthersActivity.this,LocationActivity.class);
                i.putExtra("androidId",userId);
                i.putExtra("tripId",tripId);
                startActivity(i);
            }
        });


    }
}
