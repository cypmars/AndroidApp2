package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cyprien on 12/04/2017.
 */

public class Aspect implements Parcelable{
    private int rating;
    private String type;

    public Aspect() {
    }

    protected Aspect(Parcel in) {
        rating = in.readInt();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rating);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Aspect> CREATOR = new Creator<Aspect>() {
        @Override
        public Aspect createFromParcel(Parcel in) {
            return new Aspect(in);
        }

        @Override
        public Aspect[] newArray(int size) {
            return new Aspect[size];
        }
    };

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
