package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/*
 * Created by Cyprien on 09/04/2017.
 */

public class HorairesHebdo implements Parcelable{
    private ArrayList<HorairesJour> horaires_jour ;
    private ArrayList<String> horairesHebdo;

    public HorairesHebdo(){

    }

    public HorairesHebdo(ArrayList<HorairesJour> horaires_jour, ArrayList<String> horairesHebdo) {
        this.horaires_jour = horaires_jour;
        this.horairesHebdo = horairesHebdo;
    }

    protected HorairesHebdo(Parcel in) {
        horairesHebdo = new ArrayList<>();
        in.readList(horairesHebdo, String.class.getClassLoader());
        horaires_jour = new ArrayList<>();
        in.readList(horaires_jour, HorairesJour.class.getClassLoader());
    }

    public static final Creator<HorairesHebdo> CREATOR = new Creator<HorairesHebdo>() {
        @Override
        public HorairesHebdo createFromParcel(Parcel in) {
            return new HorairesHebdo(in);
        }

        @Override
        public HorairesHebdo[] newArray(int size) {
            return new HorairesHebdo[size];
        }
    };

    public ArrayList<HorairesJour> getHoraires_jour() {
        return horaires_jour;
    }

    public void setHoraires_jour(ArrayList<HorairesJour> horaires_jour) {
        this.horaires_jour = horaires_jour;
    }

    public ArrayList<String> getHorairesHebdo() {
        return horairesHebdo;
    }

    public void setHorairesHebdo(ArrayList<String> horairesHebdo) {
        this.horairesHebdo = horairesHebdo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(horairesHebdo);
        dest.writeList(horaires_jour);
    }

    @Override
    public String toString() {
        return "HorairesHebdo{" +
                "horaires_jour=" + horaires_jour +
                ", horairesHebdo='" + horairesHebdo + '\'' +
                '}';
    }
}