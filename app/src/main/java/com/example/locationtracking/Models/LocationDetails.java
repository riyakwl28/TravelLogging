package com.example.locationtracking.Models;

public class LocationDetails {

  private Double Latitude,Longitude;
  private long CellId;

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

    public LocationDetails(Double latitude, Double longitude, long cellId) {
        Latitude = latitude;
        Longitude = longitude;
        CellId = cellId;
    }
}
