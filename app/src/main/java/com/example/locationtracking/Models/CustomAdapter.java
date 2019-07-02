package com.example.locationtracking.Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.locationtracking.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<LocationData> {
    private Context context;
    private  TextView idView;
    private  TextView distView;
    private  TextView nameView;
    private List<LocationData> list;
    private TextView timeView;

    public CustomAdapter(Context context, int resource, List<LocationData> list) {
        super(context, resource,list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LocationData item = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item, null);
        //final MediaPlayer mp= MediaPlayer.create( getContext(), item.song);

        idView = convertView.findViewById(R.id.tripid);
        idView.setText(item.trackId);
        nameView=convertView.findViewById(R.id.trackName);
        nameView.setText(item.trackName);
        distView=convertView.findViewById(R.id.distTv);
        distView.setText(item.distance);
        timeView=convertView.findViewById(R.id.timeTv);
        timeView.setText(item.time);
        return convertView;
    }

}
