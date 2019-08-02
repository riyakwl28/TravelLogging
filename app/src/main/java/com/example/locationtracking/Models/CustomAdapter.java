package com.example.locationtracking.Models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.locationtracking.Activities.LocationActivity;
import com.example.locationtracking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CustomAdapter extends ArrayAdapter<LocationData> {
    private Context context;
    private  TextView idView;
    private  TextView distView;
    private  TextView nameView;
    private List<LocationData> list;
    private TextView timeView;
    private TextView startTv,endTv;
    private ImageButton locationButton,sendBUtton;
    private ImageButton deleteBtn;


    public CustomAdapter(Context context, int resource, List<LocationData> list) {
        super(context, resource,list);
        this.context = context;
        this.list = list;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final LocationData item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item, null);


        idView = convertView.findViewById(R.id.tripid);
        idView.setText(item.trackId);
        nameView=convertView.findViewById(R.id.trackName);
        nameView.setText(item.trackName);
        distView=convertView.findViewById(R.id.distTv);
        distView.setText(item.distance);
        timeView=convertView.findViewById(R.id.timeTv);
        timeView.setText(item.time);
        startTv=convertView.findViewById(R.id.start_tv);
        startTv.setText(item.startTime);
        endTv=convertView.findViewById(R.id.end_tv);
        sendBUtton=convertView.findViewById(R.id.send_btn);
        endTv.setText(item.endTime);
        deleteBtn=convertView.findViewById(R.id.delete_button);
        final String id=item.trackId;
        final String androidId=item.deviceId;
        locationButton=convertView.findViewById(R.id.location_btn);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, LocationActivity.class);
                i.putExtra("tripId",item.trackId);
                i.putExtra("androidId",androidId);
                context.startActivity(i);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(androidId).child(id);
                        ref.removeValue();
                        list.remove(position); //or some other task
                        notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        });
        sendBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, item.trackId);
                context.startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });
        return convertView;
    }

}
