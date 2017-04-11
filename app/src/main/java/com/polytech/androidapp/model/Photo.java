package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Created by Cyprien on 09/04/2017.
 */
public class Photo implements Parcelable{
    private int height;
    private int width;
    private String reference;

    public Photo(){

    }

    public Photo(int height, int width, String reference) {
        this.height = height;
        this.width = width;
        this.reference = reference;
    }

    protected Photo(Parcel in) {
        height = in.readInt();
        width = in.readInt();
        reference = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(height);
        dest.writeInt(width);
        dest.writeString(reference);
    }

    @Override
    public String toString() {
        return "Photo{" +
                "height=" + height +
                ", width=" + width +
                ", reference='" + reference + '\'' +
                '}';
    }
}