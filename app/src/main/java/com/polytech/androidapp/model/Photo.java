package com.polytech.androidapp.model;

/*
 * Created by Cyprien on 09/04/2017.
 */
public class Photo{
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
}