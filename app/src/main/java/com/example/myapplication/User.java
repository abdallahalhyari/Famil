package com.example.myapplication;


public class User {
    String Email;
    String name;
    String phone;
    String Id;
    String locationlo;
    String locationla;
    String id_family;



    public String getId_family() {
        return id_family;
    }

    public void setId_family(String id_family) {
        this.id_family = id_family;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLocationlo() {
        return locationlo;
    }

    public void setLocationlo(String locationlo) {
        this.locationlo = locationlo;
    }

    public String getLocationla() {
        return locationla;
    }

    public void setLocationla(String locationla) {
        this.locationla = locationla;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getId() {
        return Id;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}