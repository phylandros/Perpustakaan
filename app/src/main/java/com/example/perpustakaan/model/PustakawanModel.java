package com.example.perpustakaan.model;

public class PustakawanModel {
    private String nama;
    private String gambarURL;

    public PustakawanModel(String nama, String gambarURL) {
        this.nama = nama;
        this.gambarURL = gambarURL;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getGambarURL() {
        return gambarURL;
    }

    public void setGambarURL(String gambarURL) {
        this.gambarURL = gambarURL;
    }
}

