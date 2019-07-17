package com.example.locationtracking.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.locationtracking.Models.LocationNameData;
import com.example.locationtracking.R;

import java.util.List;

public class LocationAdapter extends ArrayAdapter<LocationNameData> {
    private Context context;
    private List<LocationNameData > list;
    private TextView name_view,number_view;
    private ImageButton img_btn;


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
        name_view=convertView.findViewById(R.id.locationName);
        name_view.setText(item.locationName);
        number_view=convertView.findViewById(R.id.locationNumber);
        number_view.setText(String.valueOf(item.locationNumber));
        img_btn=convertView.findViewById(R.id.add_note_btn);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return convertView;

    }
}
