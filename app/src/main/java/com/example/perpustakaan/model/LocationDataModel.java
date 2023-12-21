package com.example.perpustakaan.model;

public class LocationDataModel {
    private int imageResource;
    private String title;
    private String location;
    private String schedule;
    private int perpusid;

    public LocationDataModel(int imageResource, int perpusid ,String title, String location, String schedule) {
        this.imageResource = imageResource;
        this.perpusid = perpusid;
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

    public int getPerpusid() {
        return perpusid;
    }

    public void setPerpusid(int perpusid) {
        this.perpusid = perpusid;
    }

}
