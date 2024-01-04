package com.example.perpustakaan.model;

public class LocationDataModel {
    private String imageUrl;
    private String title;
    private String location;
    private String schedule;
    private int perpusid;

    public LocationDataModel(String imageUrl, int perpusid , String title, String location, String schedule) {
        this.imageUrl = imageUrl;
        this.perpusid = perpusid;
        this.title = title;
        this.location = location;
        this.schedule = schedule;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
