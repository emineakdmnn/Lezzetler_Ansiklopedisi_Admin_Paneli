package com.emineakduman.lezzetansiklopedisi.model;

public class Yemek {
    private String yemekadi;
    private String malzemeler;
    private String yapilis;
    private String pufnoktasi;
    private String izlemelinki;
    private String turid;
    private String resim;

    public Yemek() {
    }

    public Yemek(String yemekadi, String malzemeler, String yapilis, String pufnoktasi, String izlemelinki, String turid, String resim) {
        this.yemekadi = yemekadi;
        this.malzemeler = malzemeler;
        this.yapilis = yapilis;
        this.pufnoktasi = pufnoktasi;
        this.izlemelinki = izlemelinki;
        this.turid = turid;
        this.resim = resim;
    }

    public String getYemekadi() {
        return yemekadi;
    }

    public void setYemekadi(String yemekadi) {
        this.yemekadi = yemekadi;
    }

    public String getMalzemeler() {
        return malzemeler;
    }

    public void setMalzemeler(String malzemeler) {
        this.malzemeler = malzemeler;
    }

    public String getYapilis() {
        return yapilis;
    }

    public void setYapilis(String yapilis) {
        this.yapilis = yapilis;
    }

    public String getPufnoktasi() {
        return pufnoktasi;
    }

    public void setPufnoktasi(String pufnoktasi) {
        this.pufnoktasi = pufnoktasi;
    }

    public String getIzlemelinki() {
        return izlemelinki;
    }

    public void setIzlemelinki(String izlemelinki) {
        this.izlemelinki = izlemelinki;
    }

    public String getTurid() {
        return turid;
    }

    public void setTurid(String turid) {
        this.turid = turid;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }
}
