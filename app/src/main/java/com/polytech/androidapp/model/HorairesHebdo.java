package com.polytech.androidapp.model;

import java.util.ArrayList;

/*
 * Created by Cyprien on 09/04/2017.
 */

public class HorairesHebdo{
    private ArrayList<HorairesJour> horaires_jour ;
    private String horairesHebdo;

    public HorairesHebdo(){

    }

    public HorairesHebdo(ArrayList<HorairesJour> horaires_jour, String horairesHebdo) {
        this.horaires_jour = horaires_jour;
        this.horairesHebdo = horairesHebdo;
    }

    public ArrayList<HorairesJour> getHoraires_jour() {
        return horaires_jour;
    }

    public void setHoraires_jour(ArrayList<HorairesJour> horaires_jour) {
        this.horaires_jour = horaires_jour;
    }

    public String getHorairesHebdo() {
        return horairesHebdo;
    }

    public void setHorairesHebdo(String horairesHebdo) {
        this.horairesHebdo = horairesHebdo;
    }
}