package com.polytech.androidapp.model;

/*
 * Created by Cyprien on 12/04/2017.
 */

public class TypeCategorie {
    private String name;
    private int imageDrawable;

    public TypeCategorie() {
    }

    public TypeCategorie(String name, int imageDrawable) {
        this.name = name;
        this.imageDrawable = imageDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }
}
