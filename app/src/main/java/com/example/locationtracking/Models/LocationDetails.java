package com.example.locationtracking.Models;

import com.google.android.gms.location.LocationCallback;

public class LocationDetails {

  private Double Latitude,Longitude;
  private long CellId;
    private String LocationName;
    private String TimeStamp;

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }



    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public long getCellId() {
        return CellId;
    }

    public void setCellId(long cellId) {
        CellId = cellId;
    }

    public LocationDetails(Double latitude, Double longitude, long cellId,String locationName,String timeStamp) {
        Latitude = latitude;
        Longitude = longitude;
        CellId = cellId;
        LocationName=locationName;
        TimeStamp=timeStamp;
    }
}
