package com.lina.braquage2;

import java.io.Serializable;

public class Alert implements Serializable{
    private String id;
    private String date;
    private String geoposition;
    private String longitude;
    private String latitude;
    private String idUser;
    private String etat;
    private String statut;

    public Alert() {
    }

    public Alert(String id, String date, String idUser) {
        this.id = id;
        this.date = date;
        this.idUser = idUser;
    }


    public Alert(String id, String date, String geoposition, String idUser) {
        this.id = id;
        this.date = date;
        this.geoposition = geoposition;
        this.idUser = idUser;
    }

    public Alert(String id, String date, String geoposition, String longitude, String latitude, String idUser) {
        this.id = id;
        this.date = date;
        this.geoposition = geoposition;
        this.longitude = longitude;
        this.latitude = latitude;
        this.idUser = idUser;
    }

    public Alert(String id, String date, String geoposition, String longitude, String latitude, String idUser, String etat) {
        this.id = id;
        this.date = date;
        this.geoposition = geoposition;
        this.longitude = longitude;
        this.latitude = latitude;
        this.idUser = idUser;
        this.etat = etat;
    }

    public Alert(String id, String date, String geoposition, String longitude, String latitude, String idUser, String etat, String statut) {
        this.id = id;
        this.date = date;
        this.geoposition = geoposition;
        this.longitude = longitude;
        this.latitude = latitude;
        this.idUser = idUser;
        this.etat = etat;
        this.statut = statut;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getGeoposition() {
        return geoposition;
    }

    public void setGeoposition(String geoposition) {
        this.geoposition = geoposition;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}
