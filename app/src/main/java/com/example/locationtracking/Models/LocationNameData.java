package com.example.locationtracking.Models;

import android.support.v4.app.INotificationSideChannel;

public class LocationNameData {
    public String locationName;
    public int locationNumber;
    public String locationId;
    public String andId;
    public String trackId;

    public String lastDistance;
    public String locTime;
    public String cellId,lac,mcc,mnc,mode;



    public LocationNameData(String locationName, int locationNumber, String locationId, String andId, String trackId, String lastDistance, String locTime, String cellId, String lac,String mcc,String mnc,String mode) {
        this.locationName = locationName;
        this.locationNumber = locationNumber;
        this.locationId=locationId;
        this.andId=andId;
        this.trackId=trackId;
        this.lastDistance=lastDistance;
        this.locTime=locTime;
        this.cellId=cellId;
        this.lac=lac;
        this.mcc=mcc;
        this.mnc=mnc;
        this.mode=mode;
    }
}
