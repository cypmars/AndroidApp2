package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/*
 * Created by Cyprien on 09/04/2017.
 */

public class Place implements Parcelable{
    private String place_id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private ArrayList<String> types;
    private int rating;
    private String phoneNumber;
    private String website;

    private HorairesHebdo horaires_hebdo;

    private Photo photoRef;

    public Place() {
        place_id="";
        name="";
        address="";
        latitude=0;
        longitude=0;
        types = new ArrayList<>();
        rating=0;
        phoneNumber="";
        website="";
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public HorairesHebdo getHoraires_hebdo() {
        return horaires_hebdo;
    }

    public void setHoraires_hebdo(HorairesHebdo horaires_hebdo) {
        this.horaires_hebdo = horaires_hebdo;
    }

    public Photo getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(Photo photoRef) {
        this.photoRef = photoRef;
    }

    public int isOpen(int dayOfWeek, int hour, int min){
        if (!horaires_hebdo.getHoraires_jour().isEmpty()){
            String fermeture = horaires_hebdo.getHoraires_jour().get(dayOfWeek).getFermeture();
            int heure_ferm = Integer.parseInt(fermeture.substring(0, 1));
            int minute_ferm = Integer.parseInt(fermeture.substring(2, 3));
            String ouverture = horaires_hebdo.getHoraires_jour().get(dayOfWeek).getOuverture();
            int heure_ouv = Integer.parseInt(ouverture.substring(0,1));
            int minute_ouv = Integer.parseInt(ouverture.substring(2,3));
            if((hour >= heure_ouv && min >= minute_ouv) || (hour <= heure_ferm && min <= minute_ferm))
                return 1;
            else
                return 0;
        }
        else
        {
            return -1;
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>()
    {
        @Override
        public Place createFromParcel(Parcel source)
        {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size)
        {
            return new Place[size];
        }
    };

    private Place(Parcel in) {
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    @Override
    public String toString() {
        return "Place{" +
                "place_id='" + place_id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", types=" + types +
                ", rating=" + rating +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", website='" + website + '\'' +
                ", horaires_hebdo=" + horaires_hebdo +
                ", photoRef=" + photoRef +
                '}';
    }
}
