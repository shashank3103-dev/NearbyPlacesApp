package com.nearbyplacesapp.models;

public class Place {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private double distance;
    private String type;

    public Place(String id, String name, double latitude, double longitude, 
                 double distance, String type) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.type = type;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public String getType() {
        return type;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", type='" + type + '\'' +
                '}';
    }
}