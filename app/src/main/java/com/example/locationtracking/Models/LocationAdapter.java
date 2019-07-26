package com.example.locationtracking.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.locationtracking.Activities.LocationDescActivity;
import com.example.locationtracking.Models.LocationNameData;
import com.example.locationtracking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.acl.LastOwnerException;
import java.util.List;

public class LocationAdapter extends ArrayAdapter<LocationNameData> {
    private Context context;
    DatabaseReference databaseReference;
    private String locationId,androidId,tripId;
    private List<LocationNameData > list;
    private TextView name_view,number_view,dist_view,time_view;
    private ImageButton img_btn;
    private String m_Text;


    public LocationAdapter(Context context, int resource, List<LocationNameData> list) {
        super(context, resource,list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LocationNameData item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_location_item, null);
        locationId=item.locationId;
        androidId=item.andId;
        tripId=item.trackId;
        dist_view=convertView.findViewById(R.id.dist_last_tv);
        dist_view.setText(item.lastDistance);
        time_view=convertView.findViewById(R.id.loc_time_tv);
        time_view.setText(item.locTime);
        name_view=convertView.findViewById(R.id.locationName);
        name_view.setText(item.locationName);
        number_view=convertView.findViewById(R.id.locationNumber);
        number_view.setText(String.valueOf(item.locationNumber));
        img_btn=convertView.findViewById(R.id.add_note_btn);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i=new Intent(context, LocationDescActivity.class);
                i.putExtra("tripId",item.trackId);
                i.putExtra("andId",item.andId);
                i.putExtra("locationId",item.locationId);
                context.startActivity(i);

            }
        });


        return convertView;

    }

    private void buildDialog() {


    }
}
