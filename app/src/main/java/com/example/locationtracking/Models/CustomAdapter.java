package com.example.locationtracking.Models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.locationtracking.Activities.LocationActivity;
import com.example.locationtracking.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<LocationData> {
    private Context context;
    private  TextView idView;
    private  TextView distView;
    private  TextView nameView;
    private List<LocationData> list;
    private TextView timeView;
    private TextView startTv,endTv;
    private Button locationButton;

    private List<String> idList;


    private List<LocationData> listSelected;
    private List<View> listSelectedRows;

    public CustomAdapter(Context context, int resource, List<LocationData> list) {
        super(context, resource,list);
        this.context = context;
        this.list = list;
        listSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
        idList=new ArrayList<>();
    }



    public void handleLongPress(int position, View view){
        final LocationData item = getItem(position);
        idList.add(item.trackId);
        if(listSelectedRows.contains(view)){
            listSelectedRows.remove(view);
            listSelected.remove(list.get(position));
            view.setBackgroundResource(R.color.white);
        }else{
            listSelected.add(list.get(position));
            listSelectedRows.add(view);
            view.setBackgroundResource(R.color.darkgray);
        }

    }



    public List<String> getIdList(){
        return idList;
    }
    public List<LocationData> getListSelected(){
        return listSelected;
    }

    public void removeSelected(){
        list.removeAll(listSelected);

        listSelected.clear();
        for(View view : listSelectedRows)
            view.setBackgroundResource(R.color.white);
        listSelectedRows.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
        endTv.setText(item.endTime);
        locationButton=convertView.findViewById(R.id.location_btn);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, LocationActivity.class);
                i.putExtra("tripId",item.trackId);
                context.startActivity(i);
            }
        });
        return convertView;
    }

}
