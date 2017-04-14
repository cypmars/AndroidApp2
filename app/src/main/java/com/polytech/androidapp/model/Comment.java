package com.polytech.androidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Cyprien on 12/04/2017.
 */

public class Comment implements Parcelable {
    private String auteur;
    private String language;
    private float rating;
    private String commentaire;
    private int time;
    private ArrayList<Aspect> aspects;

    public Comment() {
    }

    protected Comment(Parcel in) {
        auteur = in.readString();
        language = in.readString();
        rating = in.readFloat();
        commentaire = in.readString();
        time = in.readInt();
        aspects = new ArrayList<>();
        in.readList(aspects, Aspect.class.getClassLoader());

    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public ArrayList<Aspect> getAspects() {
        return aspects;
    }

    public void setAspects(ArrayList<Aspect> aspects) {
        this.aspects = aspects;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(auteur);
        dest.writeString(language);
        dest.writeFloat(rating);
        dest.writeString(commentaire);
        dest.writeInt(time);
        dest.writeList(aspects);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "auteur='" + auteur + '\'' +
                ", language='" + language + '\'' +
                ", rating=" + rating +
                ", commentaire='" + commentaire + '\'' +
                ", time=" + time +
                ", aspects=" + aspects +
                '}';
    }
}
