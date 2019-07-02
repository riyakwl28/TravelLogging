package com.example.locationtracking.Models;

public class LocationOthersDetails {

    private String trackName;

    private String distance;
    private String time;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocationOthersDetails(String trackName, String distance, String time) {
        this.trackName = trackName;
        this.distance = distance;
        this.time = time;
    }


}



