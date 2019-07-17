package com.example.locationtracking.Models;

public class LocationData {

    String trackId;
    String trackName;

    String distance;
    String time;
    String startTime;
    String endTime;

    public LocationData(String trackId,String trackName, String distance, String time,String startTime,String endTime) {
        this.trackId = trackId;
        this.distance = distance;
        this.trackName=trackName;
        this.time = time;
        this.startTime=startTime;
        this.endTime=endTime;
    }
}
