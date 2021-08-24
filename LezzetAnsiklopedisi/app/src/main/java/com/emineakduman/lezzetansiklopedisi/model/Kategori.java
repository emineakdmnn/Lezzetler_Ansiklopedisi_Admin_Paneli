package com.emineakduman.lezzetansiklopedisi.model;

public class Kategori {
    private String ad;
    private String resim;

    public Kategori() {
    }

    public Kategori(String ad, String resim) {
        this.ad = ad;
        this.resim = resim;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }
}
