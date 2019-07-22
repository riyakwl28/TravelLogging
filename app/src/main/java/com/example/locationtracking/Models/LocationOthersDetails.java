package com.example.locationtracking.Models;

public class LocationOthersDetails {

    private String trackName;
    private String time;
   private String endTime;
    private String startTime;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }



    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }




    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocationOthersDetails(String trackName, String time,String startTime,String endTime) {
        this.trackName = trackName;
        this.time = time;
        this.endTime=endTime;
        this.startTime=startTime;
    }


}



