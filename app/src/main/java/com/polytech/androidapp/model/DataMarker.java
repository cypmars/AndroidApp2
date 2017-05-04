package com.polytech.androidapp.model;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Cyprien on 04/05/2017.
 */

public class DataMarker {
    private Marker marker;
    private String name;
    private String adresse;
    private String tel;
    private String siteWeb;

    public DataMarker() {
    }

    public DataMarker(Marker marker, String name, String adresse, String tel, String siteWeb) {
        this.marker = marker;
        this.name = name;
        this.adresse = adresse;
        this.tel = tel;
        this.siteWeb = siteWeb;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }
}
