package com.example.locationtracking.Models;

public class LocationNameData {
    public String locationName;
    public int locationNumber;
    public String locationId;
    public String andId;
    public String trackId;
    public String lastDistance;
    public String locTime;


    public LocationNameData(String locationName, int locationNumber,String locationId,String andId,String trackId,String lastDistance,String locTime) {
        this.locationName = locationName;
        this.locationNumber = locationNumber;
        this.locationId=locationId;
        this.andId=andId;
        this.trackId=trackId;
        this.lastDistance=lastDistance;
        this.locTime=locTime;
    }
}
