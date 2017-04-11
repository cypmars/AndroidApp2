package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Created by Cyprien on 09/04/2017.
 */
public class HorairesJour implements Parcelable{
    private String ouverture;
    private String fermeture;

    public HorairesJour(){

    }

    protected HorairesJour(Parcel in) {
        ouverture = in.readString();
        fermeture = in.readString();
    }

    public static final Creator<HorairesJour> CREATOR = new Creator<HorairesJour>() {
        @Override
        public HorairesJour createFromParcel(Parcel in) {
            return new HorairesJour(in);
        }

        @Override
        public HorairesJour[] newArray(int size) {
            return new HorairesJour[size];
        }
    };

    public String getOuverture() {
        return ouverture;
    }

    public void setOuverture(String ouverture) {
        this.ouverture = ouverture;
    }

    public String getFermeture() {
        return fermeture;
    }

    public void setFermeture(String fermeture) {
        this.fermeture = fermeture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ouverture);
        dest.writeString(fermeture);
    }

    @Override
    public String toString() {
        return "HorairesJour{" +
                "ouverture='" + ouverture + '\'' +
                ", fermeture='" + fermeture + '\'' +
                '}';
    }
}