package com.example.perpustakaan.model;

public class LocationDataModel {
    private int imageResource;
    private String title;
    private String location;
    private String schedule;

    public LocationDataModel(int imageResource, String title, String location, String schedule) {
        this.imageResource = imageResource;
        this.title = title;
        this.location = location;
        this.schedule = schedule;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getSchedule() {
        return schedule;
    }
}
