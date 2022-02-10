package com.lina.braquage2;

public class User {

    private String id;
    private String nom;
    private String sexe;
    private String dateNaissance;
    private String email;
    //private String password;
    private String photo;

    public User(){

    }

    /*public User(String id,String nom, String sexe, String dateNaissance, String email, String password, String photo) {
        this.id = id;
        this.nom = nom;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.password = password;
        this.photo = photo;
    }*/

    public User(String id,String nom, String sexe, String dateNaissance, String email, String photo) {
        this.id = id;
        this.nom = nom;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }*/

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
