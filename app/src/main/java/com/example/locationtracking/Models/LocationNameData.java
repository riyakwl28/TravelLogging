package com.example.locationtracking.Models;

public class LocationNameData {
    public String locationName;
    public int locationNumber;
    public String locationId;
    public String andId;
    public String trackId;

    public LocationNameData(String locationName, int locationNumber,String locationId,String andId,String trackId) {
        this.locationName = locationName;
        this.locationNumber = locationNumber;
        this.locationId=locationId;
        this.andId=andId;
        this.trackId=trackId;
    }
}
