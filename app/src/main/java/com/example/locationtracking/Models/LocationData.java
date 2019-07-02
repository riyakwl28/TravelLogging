package com.example.locationtracking.Models;

public class LocationData {

    String trackId;
    String trackName;

    String distance;
    String time;

    public LocationData(String trackId,String trackName, String distance, String time) {
        this.trackId = trackId;
        this.distance = distance;
        this.trackName=trackName;
        this.time = time;
    }
}
