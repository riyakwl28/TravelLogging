package com.example.locationtracking.Models;

import com.google.android.gms.location.LocationCallback;

public class LocationDetails {

  private Double Latitude,Longitude;
  private long CellId;
    private String LocationName;
    private String TimeStamp;
    private String Distance;
    private String Start;
    private Integer Mnc,Lac,Mcc;

    public Integer getMnc() {
        return Mnc;
    }

    public void setMnc(Integer mnc) {
        Mnc = mnc;
    }

    public Integer getLac() {
        return Lac;
    }

    public void setLac(Integer lac) {
        Lac = lac;
    }

    public Integer getMcc() {
        return Mcc;
    }

    public void setMcc(Integer mcc) {
        Mcc = mcc;
    }

    public String getStart() {
        return Start;
    }

    public void setStart(String start) {
        Start = start;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

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

    public LocationDetails(Double latitude, Double longitude, long cellId,String locationName,String timeStamp,String distance,String start,Integer lac,Integer mcc,Integer mnc) {
        Latitude = latitude;
        Longitude = longitude;
        CellId = cellId;
        LocationName=locationName;
        TimeStamp=timeStamp;
        Distance=distance;
        Start=start;
        Lac=lac;
        Mcc=mcc;
        Mnc=mnc;
    }
}
