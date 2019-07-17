package com.example.locationtracking.Models;

public class DistanceData {
    private double lat1,lat2,lng1,lng2;

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLat2() {
        return lat2;
    }

    public DistanceData() {
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLng1() {
        return lng1;
    }

    public void setLng1(double lng1) {
        this.lng1 = lng1;
    }

    public double getLng2() {
        return lng2;
    }

    public void setLng2(double lng2) {
        this.lng2 = lng2;
    }

    public DistanceData(double lat1, double lat2, double lng1, double lng2) {
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.lng1 = lng1;
        this.lng2 = lng2;
    }
}
