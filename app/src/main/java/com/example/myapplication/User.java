package com.example.myapplication;


import com.google.firebase.firestore.GeoPoint;

public class User {
    String Email;
    String name;
    String phone;
    String Id;
    private GeoPoint geo_point;


    public void setId(String id) {
        Id = id;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
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