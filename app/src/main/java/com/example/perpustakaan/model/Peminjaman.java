package com.example.perpustakaan.model;


public class Peminjaman {

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

    public Peminjaman(Integer pinjamId,String namaUser, String namaBuku, String tanggalKembali) {
        this.pinjamId = pinjamId;
        this.namaUser = namaUser;
        this.namaBuku = namaBuku;
        this.tanggalKembali = tanggalKembali;
    }

    public Peminjaman() {
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


