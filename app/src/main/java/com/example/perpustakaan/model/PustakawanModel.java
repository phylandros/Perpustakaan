package com.example.perpustakaan.model;

public class PustakawanModel {
    public void setName(String name) {
        this.name = name;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    private String name;
    private int imageResource;

    public PustakawanModel(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}