package com.example.perpustakaan.model;

public class Verifikasi {

    private String namaUser;
    private String namaBuku;
    private String tanggalKembali;

    public Integer getPinjamId() {
        return pinjamId;
    }

    public void setPinjamId(Integer pinjamId) {
        this.pinjamId = pinjamId;
    }

    private Integer pinjamId;

    public Verifikasi(Integer pinjamId,String namaUser, String namaBuku, String tanggalKembali) {
        this.pinjamId = pinjamId;
        this.namaUser = namaUser;
        this.namaBuku = namaBuku;
        this.tanggalKembali = tanggalKembali;
    }

    public Verifikasi() {
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getNamaBuku() {
        return namaBuku;
    }

    public void setNamaBuku(String namaBuku) {
        this.namaBuku = namaBuku;
    }

    public String getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(String tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }
}


