package com.polytech.androidapp.model;

/**
 * Created by Laora on 17/04/2017.
 */

public class Result {
    private String place_id ;
    private String description ;

    public Result(String place_id, String description) {
        this.place_id = place_id;
        this.description = description;
    }

    public Result(String description) {
        this.description = description;
        this.place_id = null;
    }

    public Result() {
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description ;
    }
}
