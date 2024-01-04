package com.example.perpustakaan.model;

public class BukuModel {


    private int bukuid;
    private String imageUrl;
    private String judul;
    private String deskripsi;
    private boolean isSelected;

    public BukuModel() {
    }

    public BukuModel(int bukuid,String imageUrl, String judul, String deskripsi, boolean isSelected) {
        this.bukuid = bukuid;
        this.imageUrl = imageUrl;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.isSelected = false;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getBukuid() {
        return bukuid;
    }

    public void setBukuid(int bukuid) {
        this.bukuid = bukuid;
    }
}
